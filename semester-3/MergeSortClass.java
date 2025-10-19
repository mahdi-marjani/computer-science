public class MergeSortClass {

    private static <T extends Comparable<? super T>> void merge(T[] a, T[] aux, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > hi) {
                a[k] = aux[i++];
            } else if (aux[j].compareTo(aux[i]) < 0) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }

    private static <T extends Comparable<? super T>> void sort(T[] a, T[] aux, int lo, int hi) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);

        if (a[mid].compareTo(a[mid + 1]) <= 0) return;
        merge(a, aux, lo, mid, hi);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<? super T>> void sort(T[] a) {
        if (a == null || a.length < 2) return;

        T[] aux = (T[]) new Comparable[a.length];
        sort(a, aux, 0, a.length - 1);
    }

    public static void main(String[] args) {
        Integer[] arr = {5, 2, 9, 1, 5, 6};
        System.out.print("before: ");
        for (int x : arr) System.out.print(x + " ");
        System.out.println();

        MergeSortClass.sort(arr);

        System.out.print("after:  ");
        for (int x : arr) System.out.print(x + " ");
        System.out.println();
    }
}
