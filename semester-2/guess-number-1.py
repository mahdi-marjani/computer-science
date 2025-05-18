import random

guesses = []

min = 1
max = 100
guess = random.randint(min, max)
guesses.append(guess)

print(f"my guess is {guess}")

answer = input("your number is higher or lower or true? (h/l/t): ")

while answer != "t":
    if answer == "h":
        min = guess + 1
    elif answer == "l":
        max = guess - 1
    else:
        print("invalid input")
        continue

    guess = random.randint(min, max)
    guesses.append(guess)
    print(f"my guess is {guess}")
    answer = input("your number is higher or lower or true? (h/l/t): ")

print("my guesses:")
print(guesses)
print("your number is:")
print(guess)