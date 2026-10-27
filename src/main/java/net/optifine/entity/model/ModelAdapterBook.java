package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterBook extends ModelAdapter {
   public ModelAdapterBook() {
      super(TileEntityEnchantmentTable.class, "book", 0.0F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelBook();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelBook)) {
         return null;
      }

      ModelBook modelBook = (ModelBook)modelBase;
      return str.equals("cover_right")
         ? modelBook.coverRight
         : (
            str.equals("cover_left")
               ? modelBook.coverLeft
               : (
                  str.equals("pages_right")
                     ? modelBook.pagesRight
                     : (
                        str.equals("pages_left")
                           ? modelBook.pagesLeft
                           : (
                              str.equals("flipping_page_right")
                                 ? modelBook.flippingPageRight
                                 : (str.equals("flipping_page_left") ? modelBook.flippingPageLeft : (str.equals("book_spine") ? modelBook.bookSpine : null))
                           )
                     )
               )
         );
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{"cover_right", "cover_left", "pages_right", "pages_left", "flipping_page_right", "flipping_page_left", "book_spine"};
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      TileEntityRendererDispatcher instance = TileEntityRendererDispatcher.instance;
      TileEntitySpecialRenderer specialRendererByClass = instance.getSpecialRendererByClass(TileEntityEnchantmentTable.class);
      if (!(specialRendererByClass instanceof TileEntityEnchantmentTableRenderer)) {
         return null;
      }

      if (specialRendererByClass.getEntityClass() == null) {
         specialRendererByClass = new TileEntityEnchantmentTableRenderer();
         specialRendererByClass.setRendererDispatcher(instance);
      }

      if (!Reflector.TileEntityEnchantmentTableRenderer_modelBook.exists()) {
         Config.warn("Field not found: TileEntityEnchantmentTableRenderer.modelBook");
         return null;
      } else {
         Reflector.setFieldValue(specialRendererByClass, Reflector.TileEntityEnchantmentTableRenderer_modelBook, modelBase);
         return specialRendererByClass;
      }
   }
}
