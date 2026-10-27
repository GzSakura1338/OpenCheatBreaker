package net.optifine.shaders;

import java.util.ArrayList;
import java.util.List;

public class Programs {
   public List<Program> programs = new ArrayList<>();
   public Program programNone = this.make("", ProgramStage.NONE, true);

   public Program make(String str, ProgramStage programStage, Program program) {
      int size = this.programs.size();
      Program program2 = new Program(size, str, programStage, program);
      this.programs.add(program2);
      return program2;
   }

   public Program make(String str, ProgramStage programStage, boolean flag) {
      int size = this.programs.size();
      Program program = new Program(size, str, programStage, flag);
      this.programs.add(program);
      return program;
   }

   public Program makeGbuffers(String str, Program program) {
      return this.make(str, ProgramStage.GBUFFERS, program);
   }

   public Program makeComposite(String str) {
      return this.make(str, ProgramStage.COMPOSITE, this.programNone);
   }

   public Program makeDeferred(String str) {
      return this.make(str, ProgramStage.DEFERRED, this.programNone);
   }

   public Program makeShadow(String str, Program program) {
      return this.make(str, ProgramStage.SHADOW, program);
   }

   public Program makeVirtual(String str) {
      return this.make(str, ProgramStage.NONE, true);
   }

   public Program[] makeComposites(String str, int n) {
      Program[] programs = new Program[n];

      for (int i = 0; i < n; i++) {
         String str2 = i == 0 ? str : str + i;
         programs[i] = this.makeComposite(str2);
      }

      return programs;
   }

   public Program[] makeDeferreds(String str, int n) {
      Program[] programs = new Program[n];

      for (int i = 0; i < n; i++) {
         String str2 = i == 0 ? str : str + i;
         programs[i] = this.makeDeferred(str2);
      }

      return programs;
   }

   public Program getProgramNone() {
      return this.programNone;
   }

   public int getCount() {
      return this.programs.size();
   }

   public Program getProgram(String str) {
      if (str == null) {
         return null;
      }

      for (int i = 0; i < this.programs.size(); i++) {
         Program program = this.programs.get(i);
         String name = program.getName();
         if (name.equals(str)) {
            return program;
         }
      }

      return null;
   }

   public String[] getProgramNames() {
      String[] strs = new String[this.programs.size()];

      for (int i = 0; i < strs.length; i++) {
         strs[i] = this.programs.get(i).getName();
      }

      return strs;
   }

   public Program[] getPrograms() {
      return this.programs.toArray(new Program[this.programs.size()]);
   }

   public Program[] getPrograms(Program program, Program program2) {
      int index = program.getIndex();
      int index2 = program2.getIndex();
      if (index > index2) {
         int n = index;
         index = index2;
         index2 = n;
      }

      Program[] programs = new Program[index2 - index + 1];

      for (int i = 0; i < programs.length; i++) {
         programs[i] = this.programs.get(index + i);
      }

      return programs;
   }

   @Override
   public String toString() {
      return this.programs.toString();
   }
}
