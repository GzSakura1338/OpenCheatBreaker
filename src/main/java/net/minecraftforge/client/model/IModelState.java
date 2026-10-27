package net.minecraftforge.client.model;

import com.cheatbreaker.client.util.ForgeStub;
import com.google.common.base.Optional;

public interface IModelState {
   Optional<ForgeStub> apply(Optional<? extends IModelPart> optional);
}
