package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat {
   LIMB_SWING("limb_swing"),
   LIMB_SWING_SPEED("limb_speed"),
   AGE("age"),
   HEAD_YAW("head_yaw"),
   HEAD_PITCH("head_pitch"),
   SCALE("scale"),
   HEALTH("health"),
   HURT_TIME("hurt_time"),
   IDLE_TIME("idle_time"),
   MAX_HEALTH("max_health"),
   MOVE_FORWARD("move_forward"),
   MOVE_STRAFING("move_strafing"),
   PARTIAL_TICKS("partial_ticks"),
   POS_X("pos_x"),
   POS_Y("pos_y"),
   POS_Z("pos_z"),
   REVENGE_TIME("revenge_time"),
   SWING_PROGRESS("swing_progress");

   public String name;
   public RenderManager renderManager;
   public static RenderEntityParameterFloat[] VALUES = values();

   RenderEntityParameterFloat(String str) {
      this.name = str;
      this.renderManager = Minecraft.getMinecraft().getRenderManager();
   }

   public String getName() {
      return this.name;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public float eval() {
      Render renderRender = this.renderManager.renderRender;
      if (renderRender == null) {
         return 0.0F;
      }

      if (renderRender instanceof RendererLivingEntity) {
         RendererLivingEntity rendererLivingEntity = (RendererLivingEntity)renderRender;
         switch (this) {
            case LIMB_SWING:
               return rendererLivingEntity.renderLimbSwing;
            case LIMB_SWING_SPEED:
               return rendererLivingEntity.renderLimbSwingAmount;
            case AGE:
               return rendererLivingEntity.renderAgeInTicks;
            case HEAD_YAW:
               return rendererLivingEntity.renderHeadYaw;
            case HEAD_PITCH:
               return rendererLivingEntity.renderHeadPitch;
            case SCALE:
               return rendererLivingEntity.renderScaleFactor;
            default:
               EntityLivingBase renderEntity = rendererLivingEntity.renderEntity;
               if (renderEntity == null) {
                  return 0.0F;
               }

               switch (this) {
                  case HEALTH:
                     return renderEntity.getHealth();
                  case HURT_TIME:
                     return renderEntity.hurtTime;
                  case IDLE_TIME:
                     return renderEntity.getAge();
                  case MAX_HEALTH:
                     return renderEntity.getMaxHealth();
                  case MOVE_FORWARD:
                     return renderEntity.moveForward;
                  case MOVE_STRAFING:
                     return renderEntity.moveStrafing;
                  case POS_X:
                     return (float)renderEntity.posX;
                  case POS_Y:
                     return (float)renderEntity.posY;
                  case POS_Z:
                     return (float)renderEntity.posZ;
                  case REVENGE_TIME:
                     return renderEntity.getRevengeTimer();
                  case SWING_PROGRESS:
                     return renderEntity.getSwingProgress(rendererLivingEntity.renderPartialTicks);
               }
         }
      }

      return 0.0F;
   }

   public static RenderEntityParameterFloat parse(String str) {
      if (str == null) {
         return null;
      }

      for (int i = 0; i < VALUES.length; i++) {
         RenderEntityParameterFloat renderEntityParameterFloat = VALUES[i];
         if (renderEntityParameterFloat.getName().equals(str)) {
            return renderEntityParameterFloat;
         }
      }

      return null;
   }
}
