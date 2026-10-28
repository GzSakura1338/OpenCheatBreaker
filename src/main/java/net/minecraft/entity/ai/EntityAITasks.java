package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAITasks {
    public static Logger logger = LogManager.getLogger();
    public List taskEntries = new ArrayList();
    public List executingTaskEntries = new ArrayList();
    public final int tickRate = 3;
    public int tickCount;
    public Profiler theProfiler;

    public EntityAITasks(Profiler profiler) {
        this.theProfiler = profiler;
    }

    public void addTask(int n, EntityAIBase entityAIBase) {
        this.taskEntries.add(new EntityAITasks.EntityAITaskEntry(n, entityAIBase));
    }

    public void removeTask(EntityAIBase entityAIBase) {
        Iterator iterator = this.taskEntries.iterator();

        while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityAITaskEntry = (EntityAITasks.EntityAITaskEntry)iterator.next();
            EntityAIBase action = entityAITaskEntry.action;
            if (action == entityAIBase) {
                if (this.executingTaskEntries.contains(entityAITaskEntry)) {
                    action.resetTask();
                    this.executingTaskEntries.remove(entityAITaskEntry);
                }

                iterator.remove();
            }
        }
    }

    public void onUpdateTasks() {
        ArrayList list = new ArrayList();
        if (this.tickCount++ % 3 == 0) {
            for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : (Iterable<EntityAITasks.EntityAITaskEntry>)this.taskEntries) {
                boolean flag = this.executingTaskEntries.contains(entityAITaskEntry);
                if (flag) {
                    if (this.canUse(entityAITaskEntry) && this.canContinue(entityAITaskEntry)) {
                        continue;
                    }

                    entityAITaskEntry.action.resetTask();
                    this.executingTaskEntries.remove(entityAITaskEntry);
                }

                if (this.canUse(entityAITaskEntry) && entityAITaskEntry.action.shouldExecute()) {
                    list.add(entityAITaskEntry);
                    this.executingTaskEntries.add(entityAITaskEntry);
                }
            }
        } else {
            Iterator iterator = this.executingTaskEntries.iterator();

            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry entityAITaskEntry = (EntityAITasks.EntityAITaskEntry)iterator.next();
                if (!entityAITaskEntry.action.continueExecuting()) {
                    entityAITaskEntry.action.resetTask();
                    iterator.remove();
                }
            }
        }

        this.theProfiler.startSection("goalStart");

        for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : (Iterable<EntityAITasks.EntityAITaskEntry>)list) {
            this.theProfiler.startSection(entityAITaskEntry.action.getClass().getSimpleName());
            entityAITaskEntry.action.startExecuting();
            this.theProfiler.endSection();
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("goalTick");

        for (EntityAITasks.EntityAITaskEntry entityAITaskEntry : (Iterable<EntityAITasks.EntityAITaskEntry>)this.executingTaskEntries) {
            entityAITaskEntry.action.updateTask();
        }

        this.theProfiler.endSection();
    }

    public boolean canContinue(EntityAITasks.EntityAITaskEntry entityAITaskEntry) {
        this.theProfiler.startSection("canContinue");
        boolean continueExecuting = entityAITaskEntry.action.continueExecuting();
        this.theProfiler.endSection();
        return continueExecuting;
    }

    public boolean canUse(EntityAITasks.EntityAITaskEntry entityAITaskEntry) {
        this.theProfiler.startSection("canUse");

        for (EntityAITasks.EntityAITaskEntry entityAITaskEntry2 : (Iterable<EntityAITasks.EntityAITaskEntry>)this.taskEntries) {
            if (entityAITaskEntry2 != entityAITaskEntry) {
                if (entityAITaskEntry.priority >= entityAITaskEntry2.priority) {
                    if (this.executingTaskEntries.contains(entityAITaskEntry2) && !this.areTasksCompatible(entityAITaskEntry, entityAITaskEntry2)) {
                        this.theProfiler.endSection();
                        return false;
                    }
                } else if (this.executingTaskEntries.contains(entityAITaskEntry2) && !entityAITaskEntry2.action.isInterruptible()) {
                    this.theProfiler.endSection();
                    return false;
                }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

    public boolean areTasksCompatible(EntityAITasks.EntityAITaskEntry entityAITaskEntry, EntityAITasks.EntityAITaskEntry entityAITaskEntry2) {
        return (entityAITaskEntry.action.getMutexBits() & entityAITaskEntry2.action.getMutexBits()) == 0;
    }

    public class EntityAITaskEntry {
        public int priority;
        public EntityAIBase action;

        public EntityAITaskEntry(int n, EntityAIBase entityAIBase) {
            this.priority = n;
            this.action = entityAIBase;
        }
    }
}
