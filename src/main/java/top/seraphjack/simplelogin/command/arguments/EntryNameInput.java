package top.seraphjack.simplelogin.command.arguments;

public final class EntryNameInput {
    private final String input;

    private EntryNameInput(String input) {
        this.input = input;
    }

    public String getName() {
        return input;
    }

    public static EntryNameInput of(String name) {
        return new EntryNameInput(name);
    }
}
