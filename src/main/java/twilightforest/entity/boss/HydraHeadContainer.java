package twilightforest.entity.boss;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.network.PacketDistributor;
import twilightforest.TFSounds;
import twilightforest.client.particle.TFParticleType;
import twilightforest.entity.TFEntities;
import twilightforest.network.TFPacketHandler;
import twilightforest.network.ThrowPlayerPacket;
import twilightforest.util.TFDamageSources;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class holds the state data for a single hydra head
 *
 * @author Ben
 */
public class HydraHeadContainer {

	// balancing factors
	private static final int FLAME_BURN_FACTOR = 3;
	private static final int FLAME_DAMAGE = 19;
	private static final int BITE_DAMAGE = 48;
	private static double FLAME_BREATH_TRACKING_SPEED = 0.04D;

	private static final State NEXT_AUTOMATIC = null;

	enum State {
		IDLE(10),

		BITE_BEGINNING(40),
		BITE_READY(80),
		BITING(7),
		BITE_ENDING(40),

		FLAME_BEGINNING(40),
		FLAMING(100),
		FLAME_ENDING(30),

		MORTAR_BEGINNING(40),
		MORTAR_SHOOTING(25),
		MORTAR_ENDING(30),

		DYING(70),
		DEAD(20),

		ATTACK_COOLDOWN(80),

		BORN(20),
		ROAR_START(10),
		ROAR_RAWR(50);

		private static final Map<State, State> NEXT_STATE;
		public final int duration;

		State(int duration) {
			this.duration = duration;
		}

		static {
			EnumMap<State, State> b = new EnumMap<>(State.class);
			b.put(IDLE, IDLE);

			b.put(BITE_BEGINNING, BITE_READY);
			b.put(BITE_READY, BITING);
			b.put(BITING, BITE_ENDING);
			b.put(BITE_ENDING, ATTACK_COOLDOWN);

			b.put(FLAME_BEGINNING, FLAMING);
			b.put(FLAMING, FLAME_ENDING);
			b.put(FLAME_ENDING, ATTACK_COOLDOWN);

			b.put(MORTAR_BEGINNING, MORTAR_SHOOTING);
			b.put(MORTAR_SHOOTING, MORTAR_ENDING);
			b.put(MORTAR_ENDING, ATTACK_COOLDOWN);

			b.put(ATTACK_COOLDOWN, IDLE);

			b.put(DYING, DEAD);

			b.put(DEAD, DEAD);

			b.put(BORN, ROAR_START);
			b.put(ROAR_START, ROAR_RAWR);
			b.put(ROAR_RAWR, IDLE);
			NEXT_STATE = ImmutableMap.copyOf(b);
		}
	}

	public final HydraHead headEntity;
	public final HydraNeck necka;
	public final HydraNeck neckb;
	public final HydraNeck neckc;
	public final HydraNeck neckd;
	public final HydraNeck necke;

	public Entity targetEntity;

	private double targetX;
	private double targetY;
	private double targetZ;

	private State prevState;
	private State currentState;
	private State nextState;

	public boolean isSecondaryAttacking;

	private int ticksNeeded;
	private int ticksProgress;

	private final int headNum;

	private int damageTaken;
	private int respawnCounter;

	private final Hydra hydra;

	private final Map<State, Float>[] stateNeckLength;
	private final Map<State, Float>[] stateXRotations;
	private final Map<State, Float>[] stateYRotations;
	private final Map<State, Float>[] stateMouthOpen;

	@SuppressWarnings("unchecked")
	public HydraHeadContainer(Hydra hydra, int number, boolean startActive) {
		this.headNum = number;
		this.hydra = hydra;

		this.damageTaken = 0;
		this.respawnCounter = -1;

		headEntity = new HydraHead(hydra);
		headEntity.setPos(hydra.getX(), hydra.getY(), hydra.getZ());

		necka = new HydraNeck(this.headEntity);
		neckb = new HydraNeck(this.headEntity);
		neckc = new HydraNeck(this.headEntity);
		neckd = new HydraNeck(this.headEntity);
		necke = new HydraNeck(this.headEntity);

		// state positions, where is each state positioned?
		stateNeckLength = (Map<State, Float>[]) new Map<?, ?>[this.hydra.numHeads];
		stateXRotations = (Map<State, Float>[]) new Map<?, ?>[this.hydra.numHeads];
		stateYRotations = (Map<State, Float>[]) new Map<?, ?>[this.hydra.numHeads];
		stateMouthOpen = (Map<State, Float>[]) new Map<?, ?>[this.hydra.numHeads];

		for (int i = 0; i < this.hydra.numHeads; i++) {
			stateNeckLength[i] = new EnumMap<>(State.class);
			stateXRotations[i] = new EnumMap<>(State.class);
			stateYRotations[i] = new EnumMap<>(State.class);
			stateMouthOpen[i] = new EnumMap<>(State.class);
		}

		setupStateRotations();

		if (startActive) {
			this.prevState = State.IDLE;
			this.currentState = State.IDLE;
			this.nextState = NEXT_AUTOMATIC;
			this.ticksNeeded = 60;
			this.ticksProgress = 60;
		} else {
			this.prevState = State.DEAD;
			this.currentState = State.DEAD;
			this.nextState = NEXT_AUTOMATIC;
			this.ticksNeeded = 20;
			this.ticksProgress = 20;
		}
		setHeadPosition();
		setNeckPosition();
	}

	protected void setupStateRotations() {
		setAnimation(0, State.IDLE, 60, 0, 7, 0);
		setAnimation(1, State.IDLE, 10, 60, 9, 0);
		setAnimation(2, State.IDLE, 10, -60, 9, 0);
		setAnimation(3, State.IDLE, 50, 90, 8, 0);
		setAnimation(4, State.IDLE, 50, -90, 8, 0);
		setAnimation(5, State.IDLE, -10, 90, 9, 0);
		setAnimation(6, State.IDLE, -10, -90, 9, 0);

		setAnimation(0, State.ATTACK_COOLDOWN, 60, 0, 7, 0);
		setAnimation(1, State.ATTACK_COOLDOWN, 10, 60, 9, 0);
		setAnimation(2, State.ATTACK_COOLDOWN, 10, -60, 9, 0);
		setAnimation(3, State.ATTACK_COOLDOWN, 50, 90, 8, 0);
		setAnimation(4, State.ATTACK_COOLDOWN, 50, -90, 8, 0);
		setAnimation(5, State.ATTACK_COOLDOWN, -10, 90, 9, 0);
		setAnimation(6, State.ATTACK_COOLDOWN, -10, -90, 9, 0);

		setAnimation(0, State.FLAME_BEGINNING, 50, 0, 8, 0.75F);
		setAnimation(1, State.FLAME_BEGINNING, 30, 45, 9, 0.75F);
		setAnimation(2, State.FLAME_BEGINNING, 30, -45, 9, 0.75F);
		setAnimation(3, State.FLAME_BEGINNING, 50, 90, 8, 0.75F);
		setAnimation(4, State.FLAME_BEGINNING, 50, -90, 8, 0.75F);
		setAnimation(5, State.FLAME_BEGINNING, -10, 90, 9, 0.75F);
		setAnimation(6, State.FLAME_BEGINNING, -10, -90, 9, 0.75F);

		setAnimation(0, State.FLAMING, 45, 0, 8, 1);
		setAnimation(1, State.FLAMING, 30, 60, 9, 1);
		setAnimation(2, State.FLAMING, 30, -60, 9, 1);
		setAnimation(3, State.FLAMING, 50, 90, 8, 1);
		setAnimation(4, State.FLAMING, 50, -90, 8, 1);
		setAnimation(5, State.FLAMING, -10, 90, 9, 1);
		setAnimation(6, State.FLAMING, -10, -90, 9, 1);

		setAnimation(0, State.FLAME_ENDING, 60, 0, 7, 0);
		setAnimation(1, State.FLAME_ENDING, 10, 45, 9, 0);
		setAnimation(2, State.FLAME_ENDING, 10, -45, 9, 0);
		setAnimation(3, State.FLAME_ENDING, 50, 90, 8, 0);
		setAnimation(4, State.FLAME_ENDING, 50, -90, 8, 0);
		setAnimation(5, State.FLAME_ENDING, -10, 90, 9, 0);
		setAnimation(6, State.FLAME_ENDING, -10, -90, 9, 0);

		setAnimation(0, State.BITE_BEGINNING, -5, 60, 5, 0.25f);
		setAnimation(1, State.BITE_BEGINNING, -10, 60, 9, 0.25f);
		setAnimation(2, State.BITE_BEGINNING, -10, -60, 9, 0.25f);

		setAnimation(0, State.BITE_READY, -5, 60, 5, 1);
		setAnimation(1, State.BITE_READY, -10, 60, 9, 1);
		setAnimation(2, State.BITE_READY, -10, -60, 9, 1);

		setAnimation(0, State.BITING, -5, -30, 5, 0.2F);
		setAnimation(1, State.BITING, -10, -30, 5, 0.2F);
		setAnimation(2, State.BITING, -10, 30, 5, 0.2F);

		setAnimation(0, State.BITE_ENDING, 60, 0, 7, 0);
		setAnimation(1, State.BITE_ENDING, -10, 60, 9, 0);
		setAnimation(2, State.BITE_ENDING, -10, -60, 9, 0);

		setAnimation(0, State.MORTAR_BEGINNING, 50, 0, 8, 0.75F);
		setAnimation(1, State.MORTAR_BEGINNING, 30, 45, 9, 0.75F);
		setAnimation(2, State.MORTAR_BEGINNING, 30, -45, 9, 0.75F);
		setAnimation(3, State.MORTAR_BEGINNING, 50, 90, 8, 0.75F);
		setAnimation(4, State.MORTAR_BEGINNING, 50, -90, 8, 0.75F);
		setAnimation(5, State.MORTAR_BEGINNING, -10, 90, 9, 0.75F);
		setAnimation(6, State.MORTAR_BEGINNING, -10, -90, 9, 0.75F);

		setAnimation(0, State.MORTAR_SHOOTING, 45, 0, 8, 1);
		setAnimation(1, State.MORTAR_SHOOTING, 30, 60, 9, 1);
		setAnimation(2, State.MORTAR_SHOOTING, 30, -60, 9, 1);
		setAnimation(3, State.MORTAR_SHOOTING, 50, 90, 8, 1);
		setAnimation(4, State.MORTAR_SHOOTING, 50, -90, 8, 1);
		setAnimation(5, State.MORTAR_SHOOTING, -10, 90, 9, 1);
		setAnimation(6, State.MORTAR_SHOOTING, -10, -90, 9, 1);

		setAnimation(0, State.MORTAR_ENDING, 60, 0, 7, 0);
		setAnimation(1, State.MORTAR_ENDING, 10, 45, 9, 0);
		setAnimation(2, State.MORTAR_ENDING, 10, -45, 9, 0);
		setAnimation(3, State.MORTAR_ENDING, 50, 90, 8, 0);
		setAnimation(4, State.MORTAR_ENDING, 50, -90, 8, 0);
		setAnimation(5, State.MORTAR_ENDING, -10, 90, 9, 0);
		setAnimation(6, State.MORTAR_ENDING, -10, -90, 9, 0);

		setAnimation(0, State.DYING, -20, 0, 7, 0);
		setAnimation(1, State.DYING, -20, 60, 9, 0);
		setAnimation(2, State.DYING, -20, -60, 9, 0);
		setAnimation(3, State.DYING, -20, 90, 8, 0);
		setAnimation(4, State.DYING, -20, -90, 8, 0);
		setAnimation(5, State.DYING, -10, 90, 9, 0);
		setAnimation(6, State.DYING, -10, -90, 9, 0);

		setAnimation(0, State.DEAD, 0, 179, 4, 0);
		setAnimation(1, State.DEAD, 0, 179, 4, 0);
		setAnimation(2, State.DEAD, 0, -180, 4, 0);
		setAnimation(3, State.DEAD, 0, 179, 4, 0);
		setAnimation(4, State.DEAD, 0, -180, 4, 0);
		setAnimation(5, State.DEAD, 0, 179, 4, 0);
		setAnimation(6, State.DEAD, 0, -180, 4, 0);

		setAnimation(0, State.BORN, 60, 0, 7, 0);
		setAnimation(1, State.BORN, 10, 60, 9, 0);
		setAnimation(2, State.BORN, 10, -60, 9, 0);
		setAnimation(3, State.BORN, 50, 90, 8, 0);
		setAnimation(4, State.BORN, 50, -90, 8, 0);
		setAnimation(5, State.BORN, -10, 90, 9, 0);
		setAnimation(6, State.BORN, -10, -90, 9, 0);

		setAnimation(0, State.ROAR_START, 60, 0, 7, 0.25F);
		setAnimation(1, State.ROAR_START, 10, 60, 9, 0.25F);
		setAnimation(2, State.ROAR_START, 10, -60, 9, 0.25F);
		setAnimation(3, State.ROAR_START, 50, 90, 8, 0.25F);
		setAnimation(4, State.ROAR_START, 50, -90, 8, 0.25F);
		setAnimation(5, State.ROAR_START, -10, 90, 9, 0.25F);
		setAnimation(6, State.ROAR_START, -10, -90, 9, 0.25F);

		setAnimation(0, State.ROAR_RAWR, 60, 0, 9, 1);
		setAnimation(1, State.ROAR_RAWR, 10, 60, 11, 1);
		setAnimation(2, State.ROAR_RAWR, 10, -60, 11, 1);
		setAnimation(3, State.ROAR_RAWR, 50, 90, 10, 1);
		setAnimation(4, State.ROAR_RAWR, 50, -90, 10, 1);
		setAnimation(5, State.ROAR_RAWR, -10, 90, 11, 1);
		setAnimation(6, State.ROAR_RAWR, -10, -90, 11, 1);
	}

	private void setAnimation(int head, State state, float xRotation, float yRotation, float neckLength, float mouthOpen) {
		this.stateXRotations[head].put(state, xRotation);
		this.stateYRotations[head].put(state, yRotation);
		this.stateNeckLength[head].put(state, neckLength);
		this.stateMouthOpen[head].put(state, mouthOpen); // this doesn't really need to be set per-head, more per-state
	}

	public HydraNeck[] getNeckArray() {
		return new HydraNeck[]{necka, neckb, neckc, neckd, necke};
	}

	/**
	 * Called once per tick as part of the hydra entity update loop.
	 */
	public void tick() {

		headEntity.tick();
		// neck updates
		necka.tick();
		neckb.tick();
		neckc.tick();
		neckd.tick();
		necke.tick();

		// adjust for difficulty
		setDifficultyVariables();

		// only actually do these things on the server
		if (!hydra.level.isClientSide) {
			// make sure this is set up
			if (isActive() && headEntity.dimensions.width == 0) {
				headEntity.activate();
				necka.activate();
				neckb.activate();
				neckc.activate();
				neckd.activate();
				necke.activate();
			}
			else if (!isActive() && headEntity.dimensions.width > 0) {
				headEntity.deactivate();
				necka.deactivate();
				neckb.deactivate();
				neckc.deactivate();
				neckd.deactivate();
				necke.deactivate();
			}
			advanceRespawnCounter();
			advanceHeadState();
			setHeadPosition();
			setHeadFacing();
			executeAttacks();
			playSounds();
		} else {
			clientAnimateHeadDeath();
			addMouthParticles();
		}

		setNeckPosition();
	}

	public boolean canRespawn() {
		return this.currentState == State.DEAD && this.respawnCounter == -1;
	}

	private void advanceRespawnCounter() {
		if (this.currentState == State.DEAD && respawnCounter > -1) {
			if (--this.respawnCounter <= 0) {
				this.setNextState(State.BORN);
				this.damageTaken = 0;
				this.endCurrentAction();
				this.respawnCounter = -1;
			}
		}
	}

	private void clientAnimateHeadDeath() {
		// this will start the animation
		if (headEntity.getState() == State.DYING) {
			// several things, like head visibility animate off this
			this.headEntity.deathTime++;

			// make explosion particles and stuff
			if (headEntity.deathTime > 0) {
				if (headEntity.deathTime < 20) {
					doExplosionOn(headEntity, true);
				} else if (headEntity.deathTime < 30) {
					doExplosionOn(necka, false);
				} else if (headEntity.deathTime < 40) {
					doExplosionOn(neckb, false);
				} else if (headEntity.deathTime < 50) {
					doExplosionOn(neckc, false);
				} else if (headEntity.deathTime < 60) {
					doExplosionOn(neckd, false);
				} else if (headEntity.deathTime < 70) {
					doExplosionOn(necke, false);
				}
			}

			// turn necks red
			necka.hurtTime = 20;
			neckb.hurtTime = 20;
			neckc.hurtTime = 20;
			neckd.hurtTime = 20;
			necke.hurtTime = 20;

		} else {
			this.headEntity.deathTime = 0;
			this.headEntity.health = this.headEntity.maxHealth;
		}
	}

	private void doExplosionOn(HydraPart part, boolean large) {
		for (int i = 0; i < 10; ++i) {
			double vx = part.level.random.nextGaussian() * 0.02D;
			double vy = part.level.random.nextGaussian() * 0.02D;
			double vz = part.level.random.nextGaussian() * 0.02D;
			part.level.addParticle((part.level.random.nextInt(5) == 0 || large ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION), part.getX() + part.level.random.nextFloat() * part.getBbWidth() * 2.0F - part.getBbWidth(), part.getY() + part.level.random.nextFloat() * part.getBbHeight(), part.getZ() + part.level.random.nextFloat() * part.getBbWidth() * 2.0F - part.getBbWidth(), vx, vy, vz);
		}
	}

	private void advanceHeadState() {
		// check head state
		if (++ticksProgress >= this.ticksNeeded) {
			State myNext;

			// advance state
			if (this.nextState == NEXT_AUTOMATIC) {
				myNext = State.NEXT_STATE.get(this.currentState);
				if (myNext != currentState) {
					// when returning from a secondary attack, no attack cooldown
					if (this.isSecondaryAttacking && myNext == State.ATTACK_COOLDOWN) {
						this.isSecondaryAttacking = false;
						myNext = State.IDLE;
					}
				}
			} else {
				myNext = this.nextState;
				this.nextState = NEXT_AUTOMATIC;
			}

			this.ticksNeeded = this.ticksProgress = myNext.duration;
			this.ticksProgress = 0;

			this.prevState = this.currentState;
			this.currentState = myNext;
		}
		// set datawatcher so client can properly animate
		if (headEntity.getState() != this.currentState) {
			headEntity.setState(this.currentState);
		}
	}

	private void setHeadFacing() {
		if (this.currentState == State.BITE_READY) {
			// face target within certain constraints
			this.faceEntity(targetEntity, 5F, hydra.getMaxHeadXRot());

			// head 0 and 1 max yaw
			float biteMaxYaw = -60;
			float biteMinYaw = -90;

			if (headNum == 2) {
				// head 2 max/min yaw
				biteMaxYaw = 60;
				biteMinYaw = 90;
			}

			float yawOffOffset = Mth.wrapDegrees(headEntity.getYRot() - hydra.yBodyRot);

			if (yawOffOffset > biteMaxYaw) {
				headEntity.setYRot(hydra.yBodyRot + biteMaxYaw);
			}
			if (yawOffOffset < biteMinYaw) {
				headEntity.setYRot(hydra.yBodyRot + biteMinYaw);
			}

			// make the target vector be a point off in the distance in the direction we're already facing
			Vec3 look = this.headEntity.getLookAngle();
			double distance = 16.0D;
			this.targetX = headEntity.getX() + look.x * distance;
			this.targetY = headEntity.getY() + 1.5 + look.y * distance;
			this.targetZ = headEntity.getZ() + look.z * distance;

		} else if (this.currentState == State.BITING || this.currentState == State.BITE_ENDING) {
			this.faceEntity(targetEntity, 5F, hydra.getMaxHeadXRot());
			headEntity.setXRot((float) (headEntity.getXRot() + Math.PI / 4));

		} else if (this.currentState == State.ROAR_RAWR) {
			// keep facing target vector, don't move
			this.faceVec(this.targetX, this.targetY, this.targetZ, 10F, hydra.getMaxHeadXRot());

		} else if (this.currentState == State.FLAMING || this.currentState == State.FLAME_BEGINNING) {
			// move flame breath slowly towards the player
			moveTargetCoordsTowardsTargetEntity(FLAME_BREATH_TRACKING_SPEED);
			// face the target coordinates
			this.faceVec(this.targetX, this.targetY, this.targetZ, 5F, hydra.getMaxHeadXRot());

		} else {
			if (this.isActive()) {
				if (this.targetEntity != null) {
					// watch the target entity
					this.faceEntity(targetEntity, 5F, hydra.getMaxHeadXRot());
				} else {
					// while idle, look where the body is looking?
					faceIdle(1.5F, hydra.getMaxHeadXRot());
				}
			}
		}
	}

	private void moveTargetCoordsTowardsTargetEntity(double distance) {
		if (this.targetEntity != null) {
			Vec3 vect = new Vec3(this.targetEntity.getX() - this.targetX, this.targetEntity.getY() - this.targetY, this.targetEntity.getZ() - this.targetZ);

			vect = vect.normalize();

			this.targetX += vect.x * distance;
			this.targetY += vect.y * distance;
			this.targetZ += vect.z * distance;
		}
	}

	private void addMouthParticles() {
		Vec3 vector = headEntity.getLookAngle();

		double dist = 3.5;
		double px = headEntity.getX() + vector.x * dist;
		double py = headEntity.getY() + 1 + vector.y * dist;
		double pz = headEntity.getZ() + vector.z * dist;

		if (headEntity.getState() == State.FLAME_BEGINNING) {
			headEntity.level.addAlwaysVisibleParticle(ParticleTypes.FLAME, px + headEntity.level.random.nextDouble() - 0.5, py + headEntity.level.random.nextDouble() - 0.5, pz + headEntity.level.random.nextDouble() - 0.5, 0, 0, 0);
			headEntity.level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, px + headEntity.level.random.nextDouble() - 0.5, py + headEntity.level.random.nextDouble() - 0.5, pz + headEntity.level.random.nextDouble() - 0.5, 0, 0, 0);
		}

		if (headEntity.getState() == State.FLAMING) {
			Vec3 look = headEntity.getLookAngle();
			for (int i = 0; i < 5; i++) {
				double dx = look.x;
				double dy = look.y;
				double dz = look.z;

				double spread = 5 + headEntity.level.random.nextDouble() * 2.5;
				double velocity = 1.0 + headEntity.level.random.nextDouble();

				// spread flame
				dx += headEntity.level.random.nextGaussian() * 0.007499999832361937D * spread;
				dy += headEntity.level.random.nextGaussian() * 0.007499999832361937D * spread;
				dz += headEntity.level.random.nextGaussian() * 0.007499999832361937D * spread;
				dx *= velocity;
				dy *= velocity;
				dz *= velocity;

				headEntity.level.addAlwaysVisibleParticle(TFParticleType.LARGE_FLAME.get(), px, py, pz, dx, dy, dz);
			}
		}

		if (headEntity.getState() == State.BITE_BEGINNING || headEntity.getState() == State.BITE_READY) {
			headEntity.level.addAlwaysVisibleParticle(ParticleTypes.SPLASH, px + headEntity.level.random.nextDouble() - 0.5, py + headEntity.level.random.nextDouble() - 0.5, pz + headEntity.level.random.nextDouble() - 0.5, 0, 0, 0);
		}

		if (headEntity.getState() == State.MORTAR_BEGINNING) {
			headEntity.level.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, px + headEntity.level.random.nextDouble() - 0.5, py + headEntity.level.random.nextDouble() - 0.5, pz + headEntity.level.random.nextDouble() - 0.5, 0, 0, 0);
		}
	}

	private void playSounds() {
		if (headEntity.getState() == State.FLAMING && headEntity.tickCount % 5 == 0) {
			// fire breathing!
			headEntity.playSound(TFSounds.HYDRA_SHOOT_FIRE, 0.5F + headEntity.level.random.nextFloat(), headEntity.level.random.nextFloat() * 0.7F + 0.3F);
		}
		if (headEntity.getState() == State.ROAR_RAWR) {
			headEntity.playSound(TFSounds.HYDRA_ROAR, 1.25F, headEntity.level.random.nextFloat() * 0.3F + 0.7F);
		}
		if (headEntity.getState() == State.BITE_READY && this.ticksProgress == 60) {
			headEntity.playSound(TFSounds.HYDRA_WARN, 2.0F, headEntity.level.random.nextFloat() * 0.3F + 0.7F);
		}
	}

	protected void setNeckPosition() {
		// set neck positions
		Vec3 vector = null;
		float neckRotation = 0;

		if (headNum == 0) {
			vector = new Vec3(0, 3, -1);
			neckRotation = 0;
		}
		if (headNum == 1) {
			vector = new Vec3(-1, 3, 3);
			neckRotation = 90;
		}
		if (headNum == 2) {
			vector = new Vec3(1, 3, 3);
			neckRotation = -90;
		}
		if (headNum == 3) {
			vector = new Vec3(-1, 3, 3);
			neckRotation = 135;
		}
		if (headNum == 4) {
			vector = new Vec3(1, 3, 3);
			neckRotation = -135;
		}

		if (headNum == 5) {
			vector = new Vec3(-1, 3, 5);
			neckRotation = 135;
		}
		if (headNum == 6) {
			vector = new Vec3(1, 3, 5);
			neckRotation = -135;
		}

		vector = vector.yRot((-(hydra.yBodyRot + neckRotation) * 3.141593F) / 180F);
		setNeckPosition(hydra.getX() + vector.x, hydra.getY() + vector.y, hydra.getZ() + vector.z, hydra.yBodyRot, 0);
	}

	protected void setHeadPosition() {
		// set head positions
		Vec3 vector;
		double dx, dy, dz;

		// head1 is above
		// head2 is to the right
		// head3 is to the left
		//setupStateRotations(); // temporary, for debugging

		float neckLength = getCurrentNeckLength();
		float xRotation = getCurrentHeadXRotation();
		float yRotation = getCurrentHeadYRotation();


		float periodX = (headNum == 0 || headNum == 3) ? 20F : ((headNum == 1 || headNum == 4) ? 5.0f : 7.0F);
		float periodY = (headNum == 0 || headNum == 4) ? 10F : ((headNum == 1 || headNum == 6) ? 6.0f : 5.0F);

		float xSwing = Mth.sin(hydra.tickCount / periodX) * 3.0F;
		float ySwing = Mth.sin(hydra.tickCount / periodY) * 5.0F;

		if (!this.isActive()) {
			xSwing = ySwing = 0;
		}

		vector = new Vec3(0, 0, neckLength); // -53 = 3.3125
		vector = vector.xRot((xRotation * 3.141593F + xSwing) / 180F);
		vector = vector.yRot((-(hydra.yBodyRot + yRotation + ySwing) * 3.141593F) / 180F);

		dx = hydra.getX() + vector.x;
		dy = hydra.getY() + vector.y + 3;
		dz = hydra.getZ() + vector.z;

		headEntity.setPos(dx, dy, dz);
		headEntity.setMouthOpen(getCurrentMouthOpen());
	}

	private void executeAttacks() {
		if (this.currentState == State.MORTAR_SHOOTING && this.ticksProgress % 10 == 0) {

			/*if (lookTarget instanceof EntityTFHydraPart || lookTarget instanceof MultiPartEntityPart) {
				// stop hurting yourself!
				this.endCurrentAction();
			} else */{
				HydraMortarHead mortar = new HydraMortarHead(TFEntities.HYDRA_MORTAR.get(), headEntity.level, headEntity);

				// launch blasting mortars if the player is hiding
				if (this.targetEntity != null && !headEntity.canEntityBeSeen(this.targetEntity)) {
					mortar.setToBlasting();
				}

				headEntity.playSound(TFSounds.HYDRA_SHOOT, 10.0F, (headEntity.getLevel().getRandom().nextFloat() - headEntity.getLevel().getRandom().nextFloat()) * 0.2F + 1.0F);
				headEntity.level.addFreshEntity(mortar);
			}
		}
		if (headEntity.getState() == State.BITING) {
			// damage nearby things
			List<Entity> nearbyList = headEntity.level.getEntities(headEntity, headEntity.getBoundingBox().inflate(0.0, 1.0, 0.0));

			for (Entity nearby : nearbyList) {
				if (nearby instanceof LivingEntity living && nearby != hydra) {
					//is a player holding a shield? Let's do some extra stuff!
					if (nearby instanceof Player player && player.isUsingItem() && player.getUseItem().getItem().canPerformAction(player.getUseItem(), ToolActions.SHIELD_BLOCK)) {
						if (!player.getCooldowns().isOnCooldown(player.getUseItem().getItem())) {
							//cause severe damage and play a shatter sound
							headEntity.level.playSound(null, player.blockPosition(), player.getUseItem().is(Items.SHIELD) ? TFSounds.WOOD_SHIELD_SHATTERS : TFSounds.METAL_SHIELD_SHATTERS, SoundSource.PLAYERS, 1.0F, player.getVoicePitch());
							player.getUseItem().hurtAndBreak(112, player, event -> event.broadcastBreakEvent(player.getUsedItemHand()));
						}
						//add cooldown and knockback
						player.getCooldowns().addCooldown(player.getUseItem().getItem(), 200);
						TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ThrowPlayerPacket(-headEntity.getDirection().getStepX() * 0.5F, 0.15F, -headEntity.getDirection().getStepZ() * 0.5F));
					}

					// bite it!
					nearby.hurt(TFDamageSources.HYDRA_BITE, BITE_DAMAGE);

					//knockback!
					if(living instanceof Player player) {
						TFPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ThrowPlayerPacket(-headEntity.getDirection().getStepX() * 0.5F, 0.1F, -headEntity.getDirection().getStepZ() * 0.5F));
					} else {
						living.knockback(-headEntity.getDirection().getStepX(), 0.1F, -headEntity.getDirection().getStepZ());
					}
				}
			}
		}

		if (headEntity.getState() == State.FLAMING) {
			Entity target = getHeadLookTarget();

			if (target != null && target != headEntity.getParent() && (!(target instanceof HydraPart) || ((HydraPart) target).getParent() != headEntity.getParent())) {
				if (!target.fireImmune() && target.hurt(TFDamageSources.HYDRA_FIRE, FLAME_DAMAGE)) {
					target.setSecondsOnFire(FLAME_BURN_FACTOR);
				}
			}
		}
	}

	private void setDifficultyVariables() {
		if (this.hydra.level.getDifficulty() != Difficulty.HARD) {
			HydraHeadContainer.FLAME_BREATH_TRACKING_SPEED = 0.04D;
		} else {
			// hard mode!
			HydraHeadContainer.FLAME_BREATH_TRACKING_SPEED = 0.1D;  // higher is harder
		}
	}

	// TODO this seems copied from somewhere?
	@SuppressWarnings("ConstantConditions")
	@Nullable
	private Entity getHeadLookTarget() {
		Entity pointedEntity = null;
		double range = 30.0D;
		Vec3 srcVec = new Vec3(headEntity.getX(), headEntity.getY() + 1.0, headEntity.getZ());
		Vec3 lookVec = headEntity.getViewVector(1.0F);
		BlockHitResult raytrace = headEntity.level.clip(new ClipContext(srcVec, srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, headEntity));
		BlockPos hitpos = raytrace != null ? raytrace.getBlockPos() : null;
		double rx = hitpos == null ? range : Math.min(range, Math.abs(headEntity.getX() - hitpos.getX()));
		double ry = hitpos == null ? range : Math.min(range, Math.abs(headEntity.getY() - hitpos.getY()));
		double rz = hitpos == null ? range : Math.min(range, Math.abs(headEntity.getZ() - hitpos.getZ()));
		Vec3 destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
		float var9 = 3.0F;
		List<Entity> possibleList = headEntity.level.getEntities(headEntity, headEntity.getBoundingBox().move(lookVec.x * rx, lookVec.y * ry, lookVec.z * rz).inflate(var9, var9, var9));
		double hitDist = 0;

		for (Entity possibleEntity : possibleList) {
			if (possibleEntity.isPickable() && possibleEntity != headEntity && possibleEntity != necka && possibleEntity != neckb && possibleEntity != neckc) {
				float borderSize = possibleEntity.getPickRadius();
				AABB collisionBB = possibleEntity.getBoundingBox().inflate(borderSize, borderSize, borderSize);
				Optional<Vec3> interceptPos = collisionBB.clip(srcVec, destVec);

				if (collisionBB.contains(srcVec)) {
					if (0.0D < hitDist || hitDist == 0.0D) {
						pointedEntity = possibleEntity;
						hitDist = 0.0D;
					}
				} else if (interceptPos.isPresent()) {
					double possibleDist = srcVec.distanceTo(interceptPos.get());

					if (possibleDist < hitDist || hitDist == 0.0D) {
						pointedEntity = possibleEntity;
						hitDist = possibleDist;
					}
				}
			}
		}
		return pointedEntity;
	}

	public void setNextState(State next) {
		this.nextState = next;
	}

	public void endCurrentAction() {
		this.ticksProgress = this.ticksNeeded;
	}

	private float getCurrentNeckLength() {
		float prevLength = stateNeckLength[this.headNum].get(prevState);
		float curLength = stateNeckLength[this.headNum].get(currentState);
		float progress = (float) ticksProgress / (float) ticksNeeded;

		return Mth.clampedLerp(prevLength, curLength, progress);
	}

	private float getCurrentHeadXRotation() {
		float prevRotation = stateXRotations[this.headNum].get(prevState);
		float currentRotation = stateXRotations[this.headNum].get(currentState);
		float progress = (float) ticksProgress / (float) ticksNeeded;

		return Mth.clampedLerp(prevRotation, currentRotation, progress);
	}

	private float getCurrentHeadYRotation() {
		float prevRotation = stateYRotations[this.headNum].get(prevState);
		float currentRotation = stateYRotations[this.headNum].get(currentState);
		float progress = (float) ticksProgress / (float) ticksNeeded;

		return Mth.clampedLerp(prevRotation, currentRotation, progress);
	}

	protected float getCurrentMouthOpen() {
		float prevOpen = stateMouthOpen[this.headNum].get(prevState);
		float curOpen = stateMouthOpen[this.headNum].get(currentState);
		float progress = (float) ticksProgress / (float) ticksNeeded;

		return Mth.clampedLerp(prevOpen, curOpen, progress);
	}

	/**
	 * Sets the four neck positions ranging from the start position to the head position.
	 */
	protected void setNeckPosition(double startX, double startY, double startZ, float startYaw, float startPitch) {

		double endX = headEntity.getX();
		double endY = headEntity.getY();
		double endZ = headEntity.getZ();
		float endYaw = headEntity.getYRot();
		float endPitch = headEntity.getXRot();

		for (; startYaw - endYaw < -180F; endYaw -= 360F) {
		}
		for (; startYaw - endYaw >= 180F; endYaw += 360F) {
		}
		for (; startPitch - endPitch < -180F; endPitch -= 360F) {
		}
		for (; startPitch - endPitch >= 180F; endPitch += 360F) {
		}

		// translate the end position back 1 unit
		if (endPitch > 0) {
			// if we are looking down, don't raise the first neck position, it looks weird
			Vec3 vector = new Vec3(0, 0, -1.0).yRot((-endYaw * 3.141593F) / 180F);
			endX += vector.x;
			endY += vector.y;
			endZ += vector.z;
		} else {
			// but if we are looking up, lower it or it goes through the crest
			Vec3 vector = headEntity.getLookAngle();
			float dist = 1.0f;

			endX -= vector.x * dist;
			endY -= vector.y * dist;
			endZ -= vector.z * dist;

		}

		float factor;

		factor = 0.00F;
		necka.setPos(endX + (startX - endX) * factor, endY + (startY - endY) * factor, endZ + (startZ - endZ) * factor);
		necka.setYRot(endYaw + (startYaw - endYaw) * factor);
		necka.setXRot(endPitch + (startPitch - endPitch) * factor);

		factor = 0.25F;
		neckb.setPos(endX + (startX - endX) * factor, endY + (startY - endY) * factor, endZ + (startZ - endZ) * factor);
		neckb.setYRot(endYaw + (startYaw - endYaw) * factor);
		neckb.setXRot(endPitch + (startPitch - endPitch) * factor);

		factor = 0.50F;
		neckc.setPos(endX + (startX - endX) * factor, endY + (startY - endY) * factor, endZ + (startZ - endZ) * factor);
		neckc.setYRot(endYaw + (startYaw - endYaw) * factor);
		neckc.setXRot(endPitch + (startPitch - endPitch) * factor);

		factor = 0.75F;
		neckd.setPos(endX + (startX - endX) * factor, endY + (startY - endY) * factor, endZ + (startZ - endZ) * factor);
		neckd.setYRot(endYaw + (startYaw - endYaw) * factor);
		neckd.setXRot(endPitch + (startPitch - endPitch) * factor);

		factor = 1.0F;
		necke.setPos(endX + (startX - endX) * factor, endY + (startY - endY) * factor, endZ + (startZ - endZ) * factor);
		necke.setYRot(endYaw + (startYaw - endYaw) * factor);
		necke.setXRot(endPitch + (startPitch - endPitch) * factor);
	}

	private void faceIdle(float yawConstraint, float pitchConstraint) {
		float angle = (((hydra.getYRot()) * 3.141593F) / 180F);
		float distance = 30.0F;

		double dx = hydra.getX() - Mth.sin(angle) * distance;
		double dy = hydra.getY() + 3.0;
		double dz = hydra.getZ() + Mth.cos(angle) * distance;

		faceVec(dx, dy, dz, yawConstraint, pitchConstraint);
	}

	public void faceEntity(Entity entity, float yawConstraint, float pitchConstraint) {
		double yTarget;
		if (entity instanceof LivingEntity) {
			LivingEntity entityliving = (LivingEntity) entity;
			yTarget = entityliving.getY() + entityliving.getEyeHeight();
		} else {
			yTarget = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2D;
		}

		faceVec(entity.getX(), yTarget, entity.getZ(), yawConstraint, pitchConstraint);

		// let's just set the target vector here
		this.targetX = entity.getX();
		this.targetY = entity.getY();
		this.targetZ = entity.getZ();
	}

	private void faceVec(double x, double y, double z, float yawConstraint, float pitchConstraint) {
		double xOffset = x - headEntity.getX();
		double zOffset = z - headEntity.getZ();
		double yOffset = (headEntity.getY() + 1.0) - y;

		double distance = Mth.sqrt((float) (xOffset * xOffset + zOffset * zOffset));
		float xyAngle = (float) ((Math.atan2(zOffset, xOffset) * 180D) / Math.PI) - 90F;
		float zdAngle = (float) (-((Math.atan2(yOffset, distance) * 180D) / Math.PI));
		headEntity.setXRot(-updateRotation(headEntity.getXRot(), zdAngle, pitchConstraint));
		headEntity.setYRot(updateRotation(headEntity.getYRot(), xyAngle, yawConstraint));
	}

	private float updateRotation(float current, float intended, float increment) {
		float delta = Mth.wrapDegrees(intended - current);

		if (delta > increment) {
			delta = increment;
		}

		if (delta < -increment) {
			delta = -increment;
		}

		return Mth.wrapDegrees(current + delta);
	}

	public void setTargetEntity(@Nullable Entity targetEntity) {
		this.targetEntity = targetEntity;
	}

	public void setHurtTime(int hurtTime) {
		if (headEntity != null) {
			headEntity.hurtTime = hurtTime;
		}
		necka.hurtTime = hurtTime;
		neckb.hurtTime = hurtTime;
		neckc.hurtTime = hurtTime;
		neckd.hurtTime = hurtTime;
		necke.hurtTime = hurtTime;
	}

	/**
	 * At certain times, some of the heads are "dead" and hidden
	 */
	public boolean shouldRenderHead() {
		return this.headEntity.getState() != State.DEAD && this.headEntity.deathTime < 20;
	}

	/**
	 * Is this head active, that is, not dying or dead?
	 */
	public boolean isActive() {
		return this.currentState != State.DYING && this.currentState != State.DEAD;
	}

	public boolean isIdle() {
		return this.currentState == State.IDLE && (this.nextState == NEXT_AUTOMATIC || this.nextState == State.IDLE);
	}

	public boolean isAttacking() {
		return this.currentState == State.BITE_BEGINNING || this.currentState == State.BITE_READY
				|| this.currentState == State.BITING || this.currentState == State.FLAME_BEGINNING
				|| this.currentState == State.FLAMING || this.currentState == State.MORTAR_BEGINNING
				|| this.currentState == State.MORTAR_SHOOTING;
	}

	public boolean isBiting() {
		return this.currentState == State.BITE_BEGINNING || this.currentState == State.BITE_READY
				|| this.currentState == State.BITING || this.nextState == State.BITE_BEGINNING;
	}

	/**
	 * Add to our damage taken counter
	 */
	public void addDamage(float damageAmount) {
		this.damageTaken += damageAmount;
	}

	/**
	 * How much damage has this head taken?
	 */
	public int getDamageTaken() {
		return this.damageTaken;
	}

	public void setRespawnCounter(int count) {
		this.respawnCounter = count;
	}

}
