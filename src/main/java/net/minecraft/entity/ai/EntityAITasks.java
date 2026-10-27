package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
   public static Logger logger = LogManager.getLogger();
   public List<EntityAITasks.EntityAITaskEntry> taskEntries = Lists.newArrayList();
   public List<EntityAITasks.EntityAITaskEntry> executingTaskEntries = Lists.newArrayList();
   public int tickRate = 3;
   public int tickCount;
   public Profiler theProfiler;

   public EntityAITasks(Profiler profilerIn) {
      this.theProfiler = profilerIn;
   }

   public void addTask(int priority, EntityAIBase task) {
      this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(priority, task));
   }

   public void removeTask(EntityAIBase task) {
      Iterator iterator = this.taskEntries.iterator();

      while (iterator.hasNext()) {
         EntityAITasks.EntityAITaskEntry entityAITaskEntry = (EntityAITasks.EntityAITaskEntry)iterator.next();
         EntityAIBase action = entityAITaskEntry.action;
         if (action == task) {
            if (this.executingTaskEntries.contains(entityAITaskEntry)) {
               action.resetTask();
               this.executingTaskEntries.remove(entityAITaskEntry);
            }

            iterator.remove();
         }
      }
   }

   public void onUpdateTasks() {
      this.theProfiler.startSection("goalSetup");
      if (this.tickCount++ % this.tickRate == 0) {
         for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : this.taskEntries) {
            boolean flag = this.executingTaskEntries.contains(entityAITaskEntry);
            if (flag) {
               if (this.canUse(entityAITaskEntry) && this.canContinue(entityAITaskEntry)) {
                  continue;
               }

               entityAITaskEntry.action.resetTask();
               this.executingTaskEntries.remove(entityAITaskEntry);
            }

            if (this.canUse(entityAITaskEntry) && entityAITaskEntry.action.shouldExecute()) {
               entityAITaskEntry.action.startExecuting();
               this.executingTaskEntries.add(entityAITaskEntry);
            }
         }
      } else {
         Iterator iterator = this.executingTaskEntries.iterator();

         while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityAITaskEntry = (EntityAITasks.EntityAITaskEntry)iterator.next();
            if (!this.canContinue(entityAITaskEntry)) {
               entityAITaskEntry.action.resetTask();
               iterator.remove();
            }
         }
      }

      this.theProfiler.endSection();
      this.theProfiler.startSection("goalTick");

      for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : this.executingTaskEntries) {
         entityAITaskEntry.action.updateTask();
      }

      this.theProfiler.endSection();
   }

   public boolean canContinue(EntityAITasks.EntityAITaskEntry taskEntry) {
      return taskEntry.action.continueExecuting();
   }

   public boolean canUse(EntityAITasks.EntityAITaskEntry taskEntry) {
      for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : this.taskEntries) {
         if (entityAITaskEntry != taskEntry) {
            if (taskEntry.priority >= entityAITaskEntry.priority) {
               if (!this.areTasksCompatible(taskEntry, entityAITaskEntry) && this.executingTaskEntries.contains(entityAITaskEntry)) {
                  return false;
               }
            } else if (!entityAITaskEntry.action.isInterruptible() && this.executingTaskEntries.contains(entityAITaskEntry)) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry taskEntry1, EntityAITasks.EntityAITaskEntry taskEntry2) {
      return (taskEntry1.action.getMutexBits() & taskEntry2.action.getMutexBits()) == 0;
   }

   class EntityAITaskEntry {
      public int priority;
      public EntityAIBase action;

      public EntityAITaskEntry(int priorityIn, EntityAIBase task) {
         this.priority = priorityIn;
         this.action = task;
      }
   }
}
