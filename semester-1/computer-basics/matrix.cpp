#include <stdio.h>

void multiply(const int a[][10], const int b[][10], int s1, int s2, int s3, int c[][10])
{
    int i, j, k;
    for (i = 0; i < s1; i++)
    {
        for (j = 0; j < s3; j++)
        {
            int sum = 0;
            for (k = 0; k < s2; k++)
            {
                sum += (a[i][k] * b[k][j]);
            }
            c[i][j] = sum;
        }
    }
}

void sum_array(const int a[][10], const int b[][10], int s1, int s2, int c[][10])
{
    int i, j;
    for (i = 0; i < s1; i++)
    {
        for (j = 0; j < s2; j++)
        {
            c[i][j] = a[i][j] + b[i][j];
        }
    }
}

void print_matrix(int a[][10], int s1, int s2)
{
    int i, j;
    for (i = 0; i < s1; i++)
    {
        for (j = 0; j < s2; j++)
        {
            printf("%d ", a[i][j]);
        }
        printf("\n");
    }
}

void input_array(int x[][10], int &r, int &c)
{
    printf("enter num of rows and coloumns:\n");
    scanf("%d %d", &r, &c);
    int i, j;
    for (i = 0; i < r; i++)
    {
        printf("enter value of coloumns:\n");
        for (j = 0; j < c; j++)
        {
            scanf("%d", &x[i][j]);
        }
    }
}

int main()
{
    int x[10][10];
    int y[10][10];
    int m[10][10];
    int s[10][10];
    int x1, x2, y1, y2;
    input_array(x, x1, x2);
    input_array(y, y1, y2);
    multiply(x, y, x1, x2, y2, m);
    sum_array(x, y, x1, x2, s);
    printf("first matrix:\n");
    print_matrix(x, x1, x2);
    printf("second matrix:\n");
    print_matrix(y, y1, y2);
    printf("sum matrix:\n");
    print_matrix(s, x1, x2);
    printf("multiply matrix:\n");
    print_matrix(m, x1, y2);
}