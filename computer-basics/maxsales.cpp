#include <stdio.h>

float maxsales(const long int sales[][12], int companynumber, int &maxcompany){
	float average, max;
	max = 0;
	int i, j;
	for (i=0; i<companynumber; i++){
		average = 0;
		for(j=0; j<12; j++){
			average += sales[i][j];
		}
		if(average>max){
			max = average;
			maxcompany = i+1;
		}
	}
	return(max);
}

int input_sales(long int x[][12]){
	int z;
	printf("enter number of companies");
	scanf("%d", &z);
	int i, j;
	for(i=0; i<z; i++){
		printf("enter amount of sales for company %d (12 mounth):\n", i+1);
		for(j=0; j<12; j++){
			scanf("%ld", &x[i][j]);
		}
	}
	return(z);
}

int main(){
	long int a[10][12];
	int n = input_sales(a);
	int k;
	float max = maxsales(a,n,k);
	printf("max SALES = %f and name = %d", max, k);

}