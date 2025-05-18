import random

guesses = []

min = 1
max = 100
target_number = random.randint(min, max)

guess = int(input(f"what is your guess? ({min}-{max}) :"))
guesses.append(guess)

while target_number != guess:
    if target_number > guess:
        print("my number is higher")
        min = guess + 1
    elif target_number < guess:
        print("my number is lower")
        max = guess - 1
    else:
        break

    guess = int(input(f"what is your guess? ({min}-{max}) :"))
    guesses.append(guess)

print("your guesses:")
print(guesses)
print("my number is:")
print(target_number)