#include <stdio.h>

void swap(int &x, int &y)
{
    int t = x;
    x = y;
    y = t;
}

void insertion_sort(int a[], int n)
{
    int i, j, cur;
    for (i = 1; i < n; i++)
    {
        cur = a[i];
        for (j = i - 1; j >= 0; j--)
        {
            if (cur < a[j])
            {
                a[j + 1] = a[j];
            }
            else
            {
                break;
            }
        }
        a[j + 1] = cur;
    }
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
    insertion_sort(a, 10);
    printf("\nAfter sort:\n");
    for (i = 0; i < 10; i++)
    {
        printf("%d ", a[i]);
    }
}