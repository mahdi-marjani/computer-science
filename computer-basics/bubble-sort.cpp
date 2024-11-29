#include <stdio.h>

void swap(int &x, int &y)
{
    int t = x;
    x = y;
    y = t;
}

void bubble_sort(int a[], int n)
{
    int i, sw;
    do
    {
        sw = 0;
        n--;
        for (i = 0; i < n; i++)
        {
            if (a[i] > a[i + 1])
            {
                swap(a[i], a[i + 1]);
                sw = 1;
            }
        }
    } while (sw);
}

int main()
{
    int a[] = {-1, 4, 2, 9, 3, -5, 6, 19, 12, 18};
    printf("Before sort:\n");
    int i;
    for (i = 0; i < 10; i++)
    {
        printf("%d ", a[i]);
    }
    bubble_sort(a, 10);
    printf("\nAfter sort:\n");
    for (i = 0; i < 10; i++)
    {
        printf("%d ", a[i]);
    }
}