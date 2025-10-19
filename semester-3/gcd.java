public static int gcdFunc(int p, int q) {
    if (q == 0)
        return p;
    else
        return gcdFunc(q, p % q);
}

public static void main(String[] args) {
    int num1 = 10;
    int num2 = 15;
    int fact = gcdFunc(num1, num2);
    System.out.println("gcd of " + num1 + ", " + num2 + " is: " + fact);
}