package main.BankApp.dataStructure;

public interface List<T> {
    //adds element to the array
    public void add(T t);

    //adds element t to specified index, pushing following elements back
    public void add(T t, int index);

    //adds objects t to the array
    public void addAll(T... t);

    //returns element from specified index
    public T get(int index);

    //returns index of first instance of obj t
    public int indexOf(T t);

    //returns true if element t is found in the list
    public boolean contains(T t);

    //removes first instance of obj and returns it
    public T remove(T t);

    //removes obj at specified index and returns it
    public T remove(int index);

    //returns number of elements in the list
    public int size();
}
