import java.io.*;
import java.nio.file.*;
import java.util.*;

class Appointment {
    int time;
    String name;
    String service;

    Appointment(int time, String name, String service) {
        this.time = time;
        this.name = name;
        this.service = service;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d - %s (%s)",
                time / 60, time % 60, name, service);
    }
}

class AVLNode {
    Appointment app;
    AVLNode left, right;
    int height;

    AVLNode(Appointment app) {
        this.app = app;
        height = 1;
    }
}

class AVLTree {
    AVLNode root;

    int height(AVLNode n) {
        return n == null ? 0 : n.height;
    }

    int balance(AVLNode n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T = x.right;

        x.right = y;
        y.left = T;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T = y.left;

        y.left = x;
        x.right = T;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    void insert(Appointment app) {
        root = insert(root, app);
    }

    private AVLNode insert(AVLNode node, Appointment app) {
        if (node == null)
            return new AVLNode(app);

        if (app.time < node.app.time)
            node.left = insert(node.left, app);
        else if (app.time > node.app.time)
            node.right = insert(node.right, app);
        else
            return node;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int bal = balance(node);

        if (bal > 1 && app.time < node.left.app.time)
            return rotateRight(node);
        if (bal < -1 && app.time > node.right.app.time)
            return rotateLeft(node);
        if (bal > 1 && app.time > node.left.app.time) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (bal < -1 && app.time < node.right.app.time) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    AVLNode search(int time) {
        return search(root, time);
    }

    private AVLNode search(AVLNode node, int time) {
        if (node == null || node.app.time == time)
            return node;
        if (time < node.app.time)
            return search(node.left, time);
        return search(node.right, time);
    }

    void delete(int time) {
        root = delete(root, time);
    }

    private AVLNode delete(AVLNode root, int time) {
        if (root == null)
            return root;

        if (time < root.app.time)
            root.left = delete(root.left, time);
        else if (time > root.app.time)
            root.right = delete(root.right, time);
        else {
            if (root.left == null || root.right == null) {
                AVLNode temp = (root.left != null) ? root.left : root.right;
                root = temp;
            } else {
                AVLNode temp = minValueNode(root.right);
                root.app = temp.app;
                root.right = delete(root.right, temp.app.time);
            }
        }

        if (root == null)
            return root;

        root.height = Math.max(height(root.left), height(root.right)) + 1;
        int bal = balance(root);

        if (bal > 1 && balance(root.left) >= 0)
            return rotateRight(root);
        if (bal > 1 && balance(root.left) < 0) {
            root.left = rotateLeft(root.left);
            return rotateRight(root);
        }
        if (bal < -1 && balance(root.right) <= 0)
            return rotateLeft(root);
        if (bal < -1 && balance(root.right) > 0) {
            root.right = rotateRight(root.right);
            return rotateLeft(root);
        }

        return root;
    }

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    void inOrder() {
        inOrder(root);
    }

    private void inOrder(AVLNode node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println(node.app);
            inOrder(node.right);
        }
    }

    Appointment findNext(int time) {
        return findNext(root, time, null);
    }

    private Appointment findNext(AVLNode node, int time, Appointment best) {
        if (node == null)
            return best;

        if (node.app.time > time) {
            if (best == null || node.app.time < best.time)
                return findNext(node.left, time, node.app);
            return findNext(node.left, time, best);
        }
        return findNext(node.right, time, best);
    }

    void printTree() {
        printTree(root, 0);
    }

    private void printTree(AVLNode node, int level) {
        if (node == null)
            return;
        printTree(node.right, level + 1);
        System.out.println(" ".repeat(level * 4) + node.app.time);
        printTree(node.left, level + 1);
    }
}

class BNode {
    List<Appointment> keys = new ArrayList<>();
    List<BNode> children = new ArrayList<>();
    boolean leaf;

    BNode(boolean leaf) {
        this.leaf = leaf;
    }
}

class BTree {
    private BNode root;
    private int t = 2;

    void insert(Appointment app) {
        if (root == null) {
            root = new BNode(true);
            root.keys.add(app);
        } else {
            if (root.keys.size() == 2 * t - 1) {
                BNode s = new BNode(false);
                s.children.add(root);
                splitChild(s, 0);
                root = s;
            }
            insertNonFull(root, app);
        }
    }

    private void insertNonFull(BNode node, Appointment app) {
        int i = node.keys.size() - 1;

        if (node.leaf) {
            while (i >= 0 && app.time < node.keys.get(i).time)
                i--;
            node.keys.add(i + 1, app);
        } else {
            while (i >= 0 && app.time < node.keys.get(i).time)
                i--;
            i++;
            if (node.children.get(i).keys.size() == 2 * t - 1) {
                splitChild(node, i);
                if (app.time > node.keys.get(i).time)
                    i++;
            }
            insertNonFull(node.children.get(i), app);
        }
    }

    private void splitChild(BNode parent, int i) {
        BNode y = parent.children.get(i);
        BNode z = new BNode(y.leaf);

        parent.keys.add(i, y.keys.get(t - 1));
        parent.children.add(i + 1, z);

        z.keys.addAll(y.keys.subList(t, 2 * t - 1));
        y.keys.subList(t - 1, 2 * t - 1).clear();

        if (!y.leaf) {
            z.children.addAll(y.children.subList(t, 2 * t));
            y.children.subList(t, 2 * t).clear();
        }
    }

    Appointment search(int time) {
        return search(root, time);
    }

    private Appointment search(BNode node, int time) {
        if (node == null)
            return null;

        int i = 0;
        while (i < node.keys.size() && time > node.keys.get(i).time)
            i++;

        if (i < node.keys.size() && time == node.keys.get(i).time)
            return node.keys.get(i);

        if (node.leaf)
            return null;
        return search(node.children.get(i), time);
    }

    void delete(int time) {
        delete(root, time);
    }

    private void delete(BNode node, int time) {

        if (node == null)
            return;

        int idx = -1;
        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i).time == time) {
                idx = i;
                break;
            }
        }

        if (idx != -1) {
            if (node.leaf) {
                node.keys.remove(idx);
            }
        } else {
            int childIdx = 0;
            while (childIdx < node.keys.size() && time > node.keys.get(childIdx).time)
                childIdx++;
            delete(node.children.get(childIdx), time);
        }
    }

    void inOrder() {
        inOrder(root);
    }

    private void inOrder(BNode node) {
        if (node != null) {
            for (int i = 0; i < node.keys.size(); i++) {
                if (!node.leaf)
                    inOrder(node.children.get(i));
                System.out.println(node.keys.get(i));
            }
            if (!node.leaf)
                inOrder(node.children.get(node.keys.size()));
        }
    }

    Appointment findNext(int time) {
        return findNext(root, time);
    }

    private Appointment findNext(BNode node, int time) {
        if (node == null)
            return null;

        int i = 0;
        while (i < node.keys.size() && time >= node.keys.get(i).time)
            i++;

        if (i < node.keys.size()) {
            Appointment found = findNext(node.children.get(i), time);
            if (found != null)
                return found;
            return node.keys.get(i);
        }

        if (node.leaf)
            return null;
        return findNext(node.children.get(node.keys.size()), time);
    }

    void printTree() {
        printTree(root, 0);
    }

    private void printTree(BNode node, int level) {
        if (node == null)
            return;

        System.out.print(" ".repeat(level * 4) + "[");
        for (Appointment app : node.keys) {
            System.out.print(app.time + " ");
        }
        System.out.println("]");

        for (BNode child : node.children) {
            printTree(child, level + 1);
        }
    }
}

public class AppointmentSystem {
    private AVLTree avlTree = new AVLTree();
    private BTree bTree = new BTree();
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("=== Appointment Management System ===");

        while (true) {
            System.out.println("\nChoose Tree Type:");
            System.out.println("1. AVL Tree");
            System.out.println("2. B-Tree");
            System.out.println("3. Exit");
            System.out.print("> ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> avlMenu();
                case 2 -> bMenu();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void avlMenu() {
        while (true) {
            System.out.println("\n--- AVL Tree Menu ---");
            System.out.println("1. Add Appointment");
            System.out.println("2. Delete Appointment");
            System.out.println("3. Search Appointment");
            System.out.println("4. Show All (In-Order)");
            System.out.println("5. Find Next Appointment");
            System.out.println("6. Load from File");
            System.out.println("7. Show Tree Structure");
            System.out.println("8. Back to Main Menu");
            System.out.print("> ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addAppointment(true);
                case 2 -> deleteAppointment(true);
                case 3 -> searchAppointment(true);
                case 4 -> {
                    System.out.println("\nAll Appointments:");
                    avlTree.inOrder();
                }
                case 5 -> findNextAppointment(true);
                case 6 -> loadFromFile(true);
                case 7 -> {
                    System.out.println("\nAVL Tree Structure:");
                    avlTree.printTree();
                }
                case 8 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void bMenu() {
        while (true) {
            System.out.println("\n--- B-Tree Menu ---");
            System.out.println("1. Add Appointment");
            System.out.println("2. Delete Appointment");
            System.out.println("3. Search Appointment");
            System.out.println("4. Show All (In-Order)");
            System.out.println("5. Find Next Appointment");
            System.out.println("6. Load from File");
            System.out.println("7. Show Tree Structure");
            System.out.println("8. Back to Main Menu");
            System.out.print("> ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addAppointment(false);
                case 2 -> deleteAppointment(false);
                case 3 -> searchAppointment(false);
                case 4 -> {
                    System.out.println("\nAll Appointments:");
                    bTree.inOrder();
                }
                case 5 -> findNextAppointment(false);
                case 6 -> loadFromFile(false);
                case 7 -> {
                    System.out.println("\nB-Tree Structure:");
                    bTree.printTree();
                }
                case 8 -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void addAppointment(boolean isAVL) {
        System.out.print("Enter time (HH:MM): ");
        String[] timeStr = scanner.nextLine().split(":");
        int time = Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]);

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter service: ");
        String service = scanner.nextLine();

        Appointment app = new Appointment(time, name, service);

        if (isAVL) {
            avlTree.insert(app);
            System.out.println("Added to AVL Tree");
        } else {
            bTree.insert(app);
            System.out.println("Added to B-Tree");
        }
    }

    private void deleteAppointment(boolean isAVL) {
        System.out.print("Enter time to delete (HH:MM): ");
        String[] timeStr = scanner.nextLine().split(":");
        int time = Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]);

        if (isAVL) {
            avlTree.delete(time);
            System.out.println("Deleted from AVL Tree");
        } else {
            bTree.delete(time);
            System.out.println("Deleted from B-Tree");
        }
    }

    private void searchAppointment(boolean isAVL) {
        System.out.print("Enter time to search (HH:MM): ");
        String[] timeStr = scanner.nextLine().split(":");
        int time = Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]);

        Appointment found = null;
        if (isAVL) {
            AVLNode node = avlTree.search(time);
            if (node != null)
                found = node.app;
        } else {
            found = bTree.search(time);
        }

        if (found != null) {
            System.out.println("Found: " + found);
        } else {
            System.out.println("Not found");
        }
    }

    private void findNextAppointment(boolean isAVL) {
        System.out.print("Enter current time (HH:MM): ");
        String[] timeStr = scanner.nextLine().split(":");
        int time = Integer.parseInt(timeStr[0]) * 60 + Integer.parseInt(timeStr[1]);

        Appointment next = null;
        if (isAVL) {
            next = avlTree.findNext(time);
        } else {
            next = bTree.findNext(time);
        }

        if (next != null) {
            System.out.println("Next appointment: " + next);
        } else {
            System.out.println("No upcoming appointments");
        }
    }

    private void loadFromFile(boolean isAVL) {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            boolean first = true;

            for (String line : lines) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] parts = line.split(",");

                int time = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String service = parts[2].trim();

                Appointment app = new Appointment(time, name, service);

                if (isAVL) {
                    avlTree.insert(app);
                } else {
                    bTree.insert(app);
                }
            }

            System.out.println("Loaded " + (lines.size() - 1) + " appointments");

        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }

    public static void main(String[] args) {
        new AppointmentSystem().run();
    }
}