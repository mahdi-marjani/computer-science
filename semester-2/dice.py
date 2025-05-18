import random

count = 0

while True:
    dice1 = random.randint(1,6)
    dice2 = random.randint(1,6)
    dice3 = random.randint(1,6)

    count += 1

    if dice1 == dice2 and dice1 == dice3:
        print("dice:")
        dice = [dice1, dice2, dice3]
        print(dice)
        break

print("count:")
print(count)
