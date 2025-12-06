import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class Stack {
    private String[] data;
    private int top;

    public Stack() {
        data = new String[10];
        top = -1;
    }

    public void push(String text) {
        if (top == data.length - 1) {
            String[] newData = new String[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[++top] = text;
    }

    public String pop() {
        return isEmpty() ? null : data[top--];
    }

    public boolean isEmpty() {
        return top == -1;
    }
}

class CircularQueue {
    private String[] queue;
    private int front;
    private int rear;
    private int size;
    private final int capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        queue = new String[capacity];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void enqueue(String text) {
        rear = (rear + 1) % capacity;
        queue[rear] = text;
        if (size < capacity) {
            size++;
        } else {
            front = (front + 1) % capacity;
        }
    }

    public String dequeue() {
        if (isEmpty())
            return null;
        String text = queue[front];
        front = (front + 1) % capacity;
        size--;
        return text;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}

public class TextEditor {
    private String text = "";
    private Stack undoStack = new Stack();
    private Stack redoStack = new Stack();
    private CircularQueue autoSaveQueue = new CircularQueue(10);
    private boolean running = true;
    private static final String AUTO_SAVE_FILE = "autosave_recovery.txt";

    public TextEditor() {
        loadAutoSave();
        startAutoSaveThread();
    }

    private void loadAutoSave() {
        try {
            if (Files.exists(Paths.get(AUTO_SAVE_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(AUTO_SAVE_FILE)));
                String[] lines = content.split("\n");
                if (lines[lines.length - 1].startsWith("--- Auto-Save Timestamp:")) {
                    text = "";
                } else {
                    text = lines[lines.length - 1];
                }
            }
        } catch (IOException e) {
        }
    }

    private void startAutoSaveThread() {
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(10000);
                    autoSaveToFile();
                } catch (InterruptedException e) {
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void autoSaveToFile() {
        if (!autoSaveQueue.isEmpty()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(AUTO_SAVE_FILE, true))) {
                String savedText = autoSaveQueue.dequeue();
                writer.println(
                    "--- " +
                    "Auto-Save Timestamp: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    " ---"
                );
                writer.println(savedText);
            } catch (IOException e) {
                System.out.println("Auto-save error");
            }
        }
    }

    public void type(String newText) {
        if (!newText.isEmpty()) {
            undoStack.push(text);
            redoStack = new Stack();
            text += " " + newText;
            text = text.trim();

            autoSaveQueue.enqueue(text);
        }
    }

    public void delete(int n) {
        if (text.length() == 0 || n <= 0)
            return;
        undoStack.push(text);
        redoStack = new Stack();
        n = Math.min(n, text.length());
        text = text.substring(0, text.length() - n);

        autoSaveQueue.enqueue(text);
    }

    public void undo() {
        if (undoStack.isEmpty())
            return;
        redoStack.push(text);
        text = undoStack.pop();

        autoSaveQueue.enqueue(text);
    }

    public void redo() {
        if (redoStack.isEmpty())
            return;
        undoStack.push(text);
        text = redoStack.pop();

        autoSaveQueue.enqueue(text);
    }

    public void save(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.print(text);
            System.out.println("Saved to: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving");
        }
    }

    public void show() {
        System.out.println("Text: " + (text.isEmpty() ? "(empty)" : text.trim()));
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Text Editor");
        System.out.println("Commands: add <text>, del <num>, undo, redo, save <file>, show, exit");

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.startsWith("add ")) {
                editor.type(input.substring(4));
                editor.show();
            } else if (input.startsWith("del ")) {
                try {
                    editor.delete(Integer.parseInt(input.substring(4)));
                    editor.show();
                } catch (Exception e) {
                    System.out.println("Enter a number");
                }
            } else if (input.equals("undo")) {
                editor.undo();
                editor.show();
            } else if (input.equals("redo")) {
                editor.redo();
                editor.show();
            } else if (input.startsWith("save ")) {
                editor.save(input.substring(5));
            } else if (input.equals("show")) {
                editor.show();
            } else if (input.equals("exit")) {
                editor.stop();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }

        scanner.close();
    }
}