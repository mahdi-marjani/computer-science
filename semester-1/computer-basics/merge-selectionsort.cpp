#include <stdio.h>

void merge(int a[], int na, int b[], int nb, int c[], int &nc)
{
    int i, j, k;
    i = j = k = 0;
    while (i < na && j < nb)
    {
        if (a[i] < b[j])
        {
            c[k] = a[i];
            i++;
        }
        else if (a[i] > b[j])
        {
            c[k] = b[j];
            j++;
        }
        else
        {
            c[k] = a[i];
            i++;
            j++;
        }
        k++;
    }
    for (; i < na; i++, k++)
    {
        c[k] = a[i];
    }
    for (; j < nb; j++, k++)
    {
        c[k] = b[j];
    }
    nc = k;
}

void swap(int &x, int &y)
{
    int t = x;
    x = y;
    y = t;
}

void selectionsort(int a[], int n)
{
    int i, j;
    for (i = 0; i < n; i++)
    {
        for (j = i + 1; j < n; j++)
        {
            if (a[i] > a[j])
            {
                swap(a[i], a[j]);
            }
        }
    }
}

int main()
{
    int a[] = {12, -4, 2, 13, 10, 3, 17, 9};
    int b[] = {4, -5, 10, 8, 18, 11, 6};
    int c[15];
    int size;
    selectionsort(a, 8);
    selectionsort(b, 7);
    merge(a, 8, b, 7, c, size);
    for (int i = 0; i < 8; i++)
    {
        printf("%d ", a[i]);
    }
    printf("\n");
    for (int i = 0; i < 7; i++)
    {
        printf("%d ", b[i]);
    }
    printf("\nafter merge:\n");
    for (int i = 0; i < size; i++)
    {
        printf("%d ", c[i]);
    }
}