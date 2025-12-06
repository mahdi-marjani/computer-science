import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class SimpleStack {
    private String[] data;
    private int top;
    
    public SimpleStack() {
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

public class TextEditor {
    private String text = "";
    private SimpleStack undoStack = new SimpleStack();
    private SimpleStack redoStack = new SimpleStack();
    private String lastSavedText = "";
    private boolean running = true;
    private static final String AUTO_SAVE_FILE = "autosave.txt";
    
    public TextEditor() {
        loadAutoSave();
        startAutoSaveThread();
    }
    
    private void loadAutoSave() {
        try {
            if (Files.exists(Paths.get(AUTO_SAVE_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(AUTO_SAVE_FILE)));
                String[] lines = content.split("\n");
                for (int i = lines.length - 1; i >= 0; i--) {
                    if (!lines[i].isEmpty() && !lines[i].startsWith("---") && 
                        !lines[i].contains("Auto-Save")) {
                        text = lines[i];
                        lastSavedText = text;
                        break;
                    }
                }
            }
        } catch (IOException e) {}
    }
    
    private void startAutoSaveThread() {
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(10000);
                    if (!text.equals(lastSavedText) && !text.isEmpty()) {
                        saveToFile();
                        lastSavedText = text;
                    }
                } catch (InterruptedException e) {}
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUTO_SAVE_FILE, true))) {
            writer.println("---");
            writer.println("Auto-Save: " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            writer.println(text);
        } catch (IOException e) {}
    }
    
    public void type(String newText) {
        if (!newText.isEmpty()) {
            undoStack.push(text);
            redoStack = new SimpleStack();
            text += " " + newText;
            text = text.trim();
        }
    }
    
    public void delete(int n) {
        if (text.length() == 0 || n <= 0) return;
        undoStack.push(text);
        redoStack = new SimpleStack();
        n = Math.min(n, text.length());
        text = text.substring(0, text.length() - n);
    }
    
    public void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(text);
        text = undoStack.pop();
    }
    
    public void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(text);
        text = redoStack.pop();
    }
    
    public void save(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.print(text);
            lastSavedText = text;
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
        
        System.out.println("Simple Text Editor");
        System.out.println("Commands: add <text>, del <num>, undo, redo, save <file>, show, exit");
        
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            
            if (input.startsWith("add ")) {
                editor.type(input.substring(4));
                editor.show();
            }
            else if (input.startsWith("del ")) {
                try {
                    editor.delete(Integer.parseInt(input.substring(4)));
                    editor.show();
                } catch (Exception e) {
                    System.out.println("Enter a number");
                }
            }
            else if (input.equals("undo")) {
                editor.undo();
                editor.show();
            }
            else if (input.equals("redo")) {
                editor.redo();
                editor.show();
            }
            else if (input.startsWith("save ")) {
                editor.save(input.substring(5));
            }
            else if (input.equals("show")) {
                editor.show();
            }
            else if (input.equals("exit")) {
                editor.stop();
                break;
            }
            else {
                System.out.println("Unknown command");
            }
        }
        
        scanner.close();
    }
}