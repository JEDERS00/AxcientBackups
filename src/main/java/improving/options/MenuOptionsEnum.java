package improving.options;

import java.util.Optional;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public enum MenuOptionsEnum {
    INIT_COMPLETE_BACKUP(1, "Initiate a complete backup of all emails in a customers Gmail account."),
    LIST_COMPLETE_INITIATED(2, "List all backups that have been initiated."),
    EXPORT_BY_ID(3, "Return the content of a specified backup in a compressed archive."),
//    EXPORT_BY_ID_AND_LABEL(4, "Return all emails with the specified label in a specified backup, in a compressed archive."),
    EXIT(5, "Exit.");

    private int option;
    private String description;

    private MenuOptionsEnum(int option, String description) {
        this.option = option;
        this.description = description;
    }

    public static Optional<MenuOptionsEnum> getByOption(int option) {
        Optional<MenuOptionsEnum> optional = Optional.empty();
        for (MenuOptionsEnum menuOptionsEnum : MenuOptionsEnum.values()) {
            if (menuOptionsEnum.getOption() == option) {
                optional = Optional.of(menuOptionsEnum);
                break;
            }
        }
        return optional;
    }

    public int getOption() {
        return this.option;
    }

    public String getDescription() {
        return this.description;
    }

}
