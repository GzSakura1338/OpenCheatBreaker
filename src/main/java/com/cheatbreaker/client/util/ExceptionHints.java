package com.cheatbreaker.client.util;

import java.util.HashMap;

public class ExceptionHints extends HashMap<String, String> {
    public ExceptionHints() {
        this.put("NullPointerException", "A null value was found where it wasn't expected.\nThis could be due to a bug or an unexpected condition.");
        this.put("NoClassDefFoundError", "Your client tried to access a class that does not exist.\nThis could be due to deployment issues.");
        this.put(
            "IllegalStateException",
            "Your client is in a state where it cannot perform the requested operation.\nThis could be due to a bug or an unexpected condition."
        );
        this.put("UnsatisfiedLinkError", "Your client tried to load native code and failed.\nThis could be due to missing or incompatible native libraries.");
        this.put("RuntimeException", "An unexpected error occurred during runtime.\nThis could be due to a bug or an unexpected condition.");
        this.put("ClassCastException", "Your client tried to cast an object to an incompatible type.\nThis could be due to a bug or an incorrect assumption.");
        this.put(
            "ExceptionInInitializerError",
            "An error occurred during the initialization of a static variable or initializer block.\nThis could be due to a bug or an incorrect configuration."
        );
        this.put(
            "IndexOutOfBoundsException",
            "Your client tried to access an array or list with an invalid index.\nThis could be due to a bug or an incorrect calculation."
        );
        this.put(
            "ArrayIndexOutOfBoundsException",
            "Your client tried to access an array with an invalid index.\nThis could be due to a bug or an incorrect calculation."
        );
        this.put(
            "FileNotFoundException",
            "Your client tried to access a file that does not exist.\nThis could be due to a typo in the filename or the file being deleted."
        );
        this.put(
            "IOException", "An error occurred while trying to read from or write to a file.\nThis could be due to a disk error or network connectivity issues."
        );
        this.put("JsonSyntaxException", "An error occurred while trying to parse a JSON string.\nThis could be due to malformed JSON or an incorrect data type.");
        this.put("UncheckedExecutionException", "An exception was thrown by a computation in a cache loader or cache key function.\nThis could be due to a bug.");
        this.put("EOFException", "The client reached the end of a file while trying to read from it.\nThis could be due to the file being shorter than expected.");
        this.put(
            "ConcurrentModificationException",
            "The client tried to modify a collection while iterating over it.\nThis could be due to a bug or an incorrect assumption about the collection's behavior."
        );
        this.put(
            "NumberFormatException",
            "Your client tried to convert a string to a numerical value, but the string does not have the appropriate format.\nThis could be due to a bug or an incorrect data input."
        );
        this.put("IllegalArgumentException", "A method received an argument that is not valid.\nThis could be due to a bug or an incorrect data input.");
        this.put(
            "IllegalMonitorStateException",
            "Your client tried to wait on an object's monitor or to notify other threads waiting on an object's monitor without owning the specified monitor.\nThis could be due to a bug."
        );
        this.put(
            "IllegalThreadStateException",
            "Your client tried to start a thread that was already started or to stop a thread that was not started.\nThis could be due to a bug."
        );
        this.put(
            "InterruptedException",
            "A thread was interrupted by another thread while it was waiting, sleeping, or otherwise occupied, and the thread did not handle the interruption.\nThis could be due to a bug."
        );
        this.put(
            "NoSuchElementException",
            "Your client tried to access an element from an Enumeration, Iterator, or ListIterator that doesn't have any more elements.\nThis could be due to a bug or an incorrect assumption about the number of elements."
        );
        this.put(
            "UnsupportedOperationException", "Your client tried to use a method that is not supported.\nThis could be due to using outdated libraries or APIs."
        );
        this.put(
            "CloneNotSupportedException",
            "Your client tried to clone an object that does not implement the Cloneable interface.\nThis could be due to a bug or an incorrect assumption about the object's capabilities."
        );
        this.put(
            "NoSuchFieldException",
            "Your client tried to access a class's field by its string name, but the field does not exist.\nThis could be due to a typo or an incorrect assumption about the class's structure."
        );
        this.put(
            "NoSuchMethodException",
            "Your client tried to access a class's method by its string name, but the method does not exist.\nThis could be due to a typo or an incorrect assumption about the class's structure."
        );
        this.put(
            "InstantiationException",
            "Your client tried to create an instance of a class using reflection, but the class is abstract or an interface.\nThis could be due to a bug or an incorrect assumption about the class's structure."
        );
        this.put(
            "IllegalAccessException",
            "Your client tried to create an instance of a class, access a field, call a method, or access a constructor that it does not have access to.\nThis could be due to a bug or an incorrect assumption about the class's structure."
        );
        this.put(
            "InvocationTargetException",
            "An exception was thrown by an invoked method or constructor accessed via reflection.\nThis could be due to a bug in the invoked method or constructor."
        );
    }
}
