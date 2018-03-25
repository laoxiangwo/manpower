package loader.util;

import java.util.List;

public interface ITopK<T> {

    /**
     * offer a single element to the top.
     *
     * @param element - the element to add to the top
     * @return false if the element was already in the top
     */
    boolean offer(T element);

    /**
     * offer a single element to the top and increment the count
     * for that element by incrementCount.
     *
     * @param element        - the element to add to the top
     * @param incrementCount - the increment count for the given count
     * @return false if the element was already in the top
     */
    boolean offer(T element, int incrementCount);

    /**
     * @param k
     * @return top k elements offered (may be an approximation)
     */
    List<T> peek(int k);
}
