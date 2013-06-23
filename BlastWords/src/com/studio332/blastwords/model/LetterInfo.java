package com.studio332.blastwords.model;

public class LetterInfo {
   public enum Type {NORMAL, LOCKED}
   private final Type type;
   private final Character character;
   
   public LetterInfo(Type t, Character c) {
      this.type = t;
      this.character = c;
   }

   public Type getType() {
      return type;
   }

   public Character getCharacter() {
      return character;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result
            + ((character == null) ? 0 : character.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LetterInfo other = (LetterInfo) obj;
      if (character == null) {
         if (other.character != null)
            return false;
      } else if (!character.equals(other.character))
         return false;
      if (type != other.type)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "LetterInfo [type=" + type + ", character=" + character + "]";
   }
}
