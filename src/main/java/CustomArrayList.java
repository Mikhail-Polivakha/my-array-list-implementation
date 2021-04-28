import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomArrayList<T> implements List<T> {

    private static final int DEFAULT_ALLOCATION_SIZE = 16;
    private Object[] sourceArray;
    private int size;

    public CustomArrayList(Object[] sourceArray) {
        this.sourceArray = sourceArray;
        this.size = sourceArray.length;
    }

    public CustomArrayList() {
        this.size = 0;
    }

    public CustomArrayList(int size) {
        if (size < 0) throw new IllegalArgumentException("Size to bee expected as positive integer, but was " + size);
        sourceArray = new Object[size];
    }

    @Override
    public int size() {
        return this.size;
    }

    private class CustomIterator implements Iterator<T> {

        int currentPosition;

        public CustomIterator() {
            this(0);
        }

        public CustomIterator(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public boolean hasNext() {
            return size - 1 > currentPosition;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("next element is absent");
            }
            return (T) sourceArray[++currentPosition];
        }
    }

    @Override
    public boolean isEmpty() {
        return Objects.isNull(sourceArray) || size == 0;
    }

    @Override
    public boolean contains(Object object) {
        return Objects.nonNull(sourceArray) && Arrays.stream(sourceArray).anyMatch(object::equals);
    }

    @Override
    public Iterator<T> iterator() {
        return new CustomIterator();
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(sourceArray, size);
    }

    @Override
    public <T1> T1[] toArray(T1[] elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T elementToAdd) {
        if (Objects.nonNull(sourceArray[size - 1])) {
            increaseArraySize(1);
        }
        return add(elementToAdd, sourceArray);
    }

    private void increaseArraySize(Integer amountOfNewCells) {
        sourceArray = allocateNewArrayWithSize(calculateNewSize(amountOfNewCells));
    }

    private Object[] allocateNewArrayWithSize(int newSize) {
        final Object[] newArrayOfElement = new Object[newSize];
        for (int i = 0; i < size; i++) {
            newArrayOfElement[i] = sourceArray[i];
        }
        sourceArray = newArrayOfElement;
        return sourceArray;
    }

    private int calculateNewSize(Integer amountOfNewCellsToAdd) {
        return amountOfNewCellsToAdd == null
                ? (int) (size * 1.25) //default increase is 25 %
                : size + amountOfNewCellsToAdd;
    }

    private boolean add(T element, Object[] arrayInWhichToAdd) {
        for (int i = 0; i < arrayInWhichToAdd.length; i++) {
            if (Objects.isNull(arrayInWhichToAdd[i])) {
                arrayInWhichToAdd[i] = element;
                size++;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object object) {
        for (int i = 0; i < size; i++) {
            if (sourceArray[i].equals(object)) {
                this.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return collection.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean hasListBeenChanged = false;
        for (T element : collection) {
            final boolean hasCurrentElementBeenAppend = this.add(element);
            if (!hasListBeenChanged && hasCurrentElementBeenAppend) {
                hasListBeenChanged = true;
            }
        }
        return hasListBeenChanged;
    }

    @Override
    public boolean addAll(int startIndex, Collection<? extends T> collection) {
        checkIndexIsValidElseThrow(startIndex);

        final Object[] elementsToAdd = collection.toArray();
        final int newArrayLength = this.size + elementsToAdd.length;

        if (newArrayLength >= sourceArray.length) {
            increaseArraySize(newArrayLength - sourceArray.length);
        }

        final Object[] lastElements = Arrays.copyOfRange(sourceArray, startIndex, size);

        size = sourceArray.length;
        int indexOfElementsToBeAppend = 0;
        for (int i = startIndex; i < startIndex + elementsToAdd.length; i++) {
            sourceArray[i] = elementsToAdd[indexOfElementsToBeAppend++];
        }
        int indexOfLastElement = 0;
        for (int i = startIndex + elementsToAdd.length; i < size; i++) {
            sourceArray[i] = lastElements[indexOfLastElement++];
        }
        return !collection.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        AtomicBoolean hasListBeenChanged = new AtomicBoolean(false);
        collection.forEach(element -> {
            if (this.contains(element)) {
                hasListBeenChanged.set(true);
                this.remove(element);
            }
        });
        return hasListBeenChanged.get();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        int[] indexesOfElementsToRetain = new int[collection.size()];
        int indexOfIndexesArray = 0;
        for (Object element : collection) {
            final Integer indexOfCurrentElementInSourceArray = this.getFirstIndexOf(element);
            if (indexOfCurrentElementInSourceArray != null) {
                indexesOfElementsToRetain[indexOfIndexesArray++] = indexOfCurrentElementInSourceArray;
            }
        }
        return retainElementsOnlyWithIndexesAsPassed(indexesOfElementsToRetain);
    }

    private boolean retainElementsOnlyWithIndexesAsPassed(int[] indexesOfElementsToRetain) {
        final int previousArrayLength = size;
        sourceArray = new Object[indexesOfElementsToRetain.length];
        for (int indexOfElementToRetain : indexesOfElementsToRetain) {
            this.add(this.get(indexOfElementToRetain));
        }
        size = indexesOfElementsToRetain.length;
        return indexesOfElementsToRetain.length != previousArrayLength;
    }

    private Integer getFirstIndexOf(Object element) {
        return getIndexOfDependOnOrder(element, Order.FIRST);
    }

    private Integer getLastIndexOf(Object element) {
        return getIndexOfDependOnOrder(element, Order.LAST);
    }

    private Integer getIndexOfDependOnOrder(Object element, Order order) {
        if (order.equals(Order.LAST)) {
            for (int i = size - 1; i >= 0; i--) {
                if (sourceArray[i].equals(element)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (sourceArray[i].equals(element)) {
                    return i;
                }
            }
        }
        return null;
    }

    private enum Order {
        LAST, FIRST;
    }

    @Override
    public void clear() {
        sourceArray = new Object[DEFAULT_ALLOCATION_SIZE];
        size = 0;
    }

    @Override
    public T get(int index) {
        checkIndexIsValidElseThrow(index);
        return (T) sourceArray[index];
    }

    @Override
    public T set(int index, T element) {
        checkIndexIsValidElseThrow(index);
        Object previousElement = sourceArray[index];
        sourceArray[index] = element;
        return (T) previousElement;
    }

    private void checkIndexIsValidElseThrow(int index) {
        if (size <= index)
            throw new IndexOutOfBoundsException("passed 'index' is out of range [0.." + (size - 1) + "]");
    }

    @Override
    public void add(int indexOfInsertion, T element) {
        checkIndexIsValidElseThrow(indexOfInsertion);
        increaseSizeIfNecessary();
        shiftToRightArrayFromIndex(indexOfInsertion);
        sourceArray[indexOfInsertion] = element;
        size++;
    }

    private void shiftToRightArrayFromIndex(int indexOfInsertion) {
        Object previousElement = sourceArray[indexOfInsertion];
        for (int currentIndex = indexOfInsertion + 1; currentIndex < sourceArray.length; currentIndex++) {
            Object temporaryValue = sourceArray[currentIndex];
            sourceArray[currentIndex] = previousElement;
            previousElement = temporaryValue;
        }
    }

    private void increaseSizeIfNecessary() {
        if (sourceArray.length == this.size) increaseArraySize(1);
    }

    @Override
    public T remove(int index) {
        checkIndexIsValidElseThrow(index);
        final Object removedElement = sourceArray[index];
        for (int i = index; i < this.size - 1; i++) {
            sourceArray[i] = sourceArray[i + 1];
        }
        this.size--;
        return (T) removedElement;
    }

    @Override
    public int indexOf(Object element) {
        final Integer index = getFirstIndexOf(element);
        return Objects.nonNull(index) ? index : -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        final Integer lastIndexOf = getLastIndexOf(element);
        return Objects.nonNull(lastIndexOf) ? lastIndexOf : -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new CustomArrayListListIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new CustomArrayListListIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        validateIndexes(fromIndex, toIndex);
        final Object[] subArray = new Object[toIndex - fromIndex];
        for (int i = fromIndex; i < toIndex; i++) {
            subArray[i] = sourceArray[i];
        }
        return new CustomArrayList<>(subArray);
    }

    private void validateIndexes(int fromIndex, int toIndex) {
        checkIndexIsValidElseThrow(toIndex);
        checkIndexIsValidElseThrow(fromIndex);
        if (fromIndex < toIndex) {
            throw new IndexOutOfBoundsException("'fromIndex' should be less or equal in comparison with 'toIndex', for now fromIndex is '"
                                                        + fromIndex + "', toIndex is '" + toIndex + "'");
        }
    }


    private class CustomArrayListListIterator implements ListIterator<T> {

        private boolean wasModified;
        private int currentPosition;
        private boolean haveMovementMethodInvoked;

        public CustomArrayListListIterator() {
            this.currentPosition = -1;
            this.wasModified = false;
        }

        public CustomArrayListListIterator(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        @Override
        public boolean hasNext() {
            return size - 1 > currentPosition;
        }

        @Override
        public T next() {
            if (!this.hasNext()) throw new NoSuchElementException("No next element");
            haveMovementMethodInvoked = true;
            wasModified = false;
            return (T) sourceArray[++currentPosition];
        }

        @Override
        public boolean hasPrevious() {
            return currentPosition > 0;
        }

        @Override
        public T previous() {
            if (!this.hasPrevious()) throw new NoSuchElementException("No previous element");
            haveMovementMethodInvoked = true;
            wasModified = false;
            return (T) sourceArray[--currentPosition];
        }

        @Override
        public int nextIndex() {
            return Math.max(currentPosition + 1, size);
        }

        @Override
        public int previousIndex() {
            return currentPosition > 0 ? currentPosition - 1 : -1;
        }

        @Override
        public void remove() {
            if (wasModified) throw new IllegalStateException("'add' method was already been called on this element");
            checkNextOrPreviousMethodsInvocationsExistance();
            CustomArrayList.this.remove(currentPosition);
            wasModified = true;
        }

        @Override
        public void set(T element) {
            if (wasModified) throw new IllegalStateException("'add' or 'remove' methods have been already invoked");
            checkNextOrPreviousMethodsInvocationsExistance();
            CustomArrayList.this.set(currentPosition, element);
        }

        private void checkNextOrPreviousMethodsInvocationsExistance() {
            if (!haveMovementMethodInvoked) throw new IllegalStateException("'previous' or 'next' methods have not been called yet");
        }

        @Override
        public void add(T element) {
            if (wasModified) throw new IllegalStateException("'remove' method was already been called on this element");
            checkNextOrPreviousMethodsInvocationsExistance();
            CustomArrayList.this.add(currentPosition, element);
            wasModified = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomArrayList<?> that = (CustomArrayList<?>) o;
        return size == that.size &&
                Arrays.equals(sourceArray, that.sourceArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(sourceArray);
        return result;
    }
}