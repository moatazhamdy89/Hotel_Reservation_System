
package gui.controller.guest;

import com.hotel.manager.RoomManager;
import com.hotel.model.*;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import gui.navigation.SceneManager;

public class GuestMainController {

    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private ComboBox<Integer> guestsCombo;
    @FXML private Button searchButton;
    @FXML private VBox roomsContainer;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;

    private SceneManager sceneManager;

    private Guest currentGuest;

    private final RoomManager roomManager = new RoomManager();

    private List<Room> allRooms = new ArrayList<>();

    private static final BigDecimal MAX_PRICE =
            new BigDecimal("9999999999.99");

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setGuest(Guest guest) {

        this.currentGuest = guest;

        if (guest != null) {

            welcomeLabel.setText(
                    "Welcome, " + guest.getFirstName()
                            + " " + guest.getLastName() + "!"
            );

        } else {

            welcomeLabel.setText("Welcome!");
        }
    }

    @FXML
    private void initialize() {

        roomTypeCombo.getItems().addAll(
                "All Types", "Single", "Double", "Suite", "VIP"
        );
        roomTypeCombo.setValue("All Types");

        for (int i = 1; i <= 20; i++) {
            guestsCombo.getItems().add(i);
        }
        guestsCombo.setValue(2);

        checkInPicker.setValue(LocalDate.now().plusDays(1));
        checkOutPicker.setValue(LocalDate.now().plusDays(2));

        loadRooms();
    }

    private void loadRooms() {

        statusLabel.setText("Loading rooms from database...");

        Task<List<Room>> task = new Task<>() {
            @Override
            protected List<Room> call() {
                return roomManager.findAllRooms();
            }
        };

        task.setOnSucceeded(event -> {
            allRooms = task.getValue();
            showAllRooms();
        });

        task.setOnFailed(event -> {
            statusLabel.setText(
                    "Could not load rooms. Check database connection."
            );
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task, "load-rooms");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void showAllRooms() {

        roomTypeCombo.setValue("All Types");

        statusLabel.setText(
                "Browsing " + allRooms.size()
                        + " rooms. Availability is checked when you reserve."
        );

        displayRooms(allRooms);
    }

    private void displayRooms(List<Room> rooms) {

        roomsContainer.getChildren().clear();

        if (rooms.isEmpty()) {
            roomsContainer.getChildren().add(
                    new Label("No rooms found.")
            );
            return;
        }

        for (Room room : rooms) {
            roomsContainer.getChildren().add(
                    createRoomCard(room)
            );
        }
    }

    private HBox createRoomCard(Room room) {

        String type = typeName(room);

        HBox card = new HBox(18);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("guest-room-card");

        // ROOM IMAGE
        StackPane picture = new StackPane();

        picture.setMinSize(205, 145);
        picture.setPrefSize(205, 145);
        picture.setMaxSize(205, 145);

        picture.getStyleClass().add("room-image-placeholder");

        String path =
                "/images/rooms/" + type.toLowerCase() + ".jpg";

        java.net.URL url = getClass().getResource(path);

        if (url != null) {

            ImageView image = new ImageView(
                    new Image(url.toExternalForm())
            );

            image.setFitWidth(205);
            image.setFitHeight(145);
            image.setPreserveRatio(false);

            picture.getChildren().add(image);

        } else {
            picture.getChildren().add(
                    new Label(type + " Room")
            );
        }

        // ROOM DETAILS
        Label title = new Label(type + " Room");
        title.getStyleClass().add("room-title");

        Label description = new Label(roomDescription(room));
        description.setWrapText(true);
        description.getStyleClass().add("room-description");

        Label details = new Label(
                "Up to " + room.getMaxGuests()
                        + " guests  |  View: "
                        + room.getViewType()
        );
        details.getStyleClass().add("room-details");

        VBox info = new VBox(12, title, description, details);

        HBox.setHgrow(info, Priority.ALWAYS);
        info.setMaxWidth(Double.MAX_VALUE);

        // PRICE
        Label price = new Label(
                room.getBasePrice() == null
                        ? "Price unavailable"
                        : room.getBasePrice().toPlainString()
                        + " EGP base / night"
        );

        price.getStyleClass().add("room-price");
        price.setWrapText(true);

        // RESERVE BUTTON
        Button reserve = new Button("Reserve");
        reserve.getStyleClass().add("reserve-button");
        reserve.setPrefSize(115, 38);

        reserve.setOnAction(event -> reserveType(room));

        VBox right = new VBox(20, price, reserve);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(170);

        card.getChildren().addAll(picture, info, right);

        return card;
    }

    private String roomDescription(Room room) {
        String description = room.getDescription();

        return description == null || description.isBlank()
                ? "No description available."
                : description;
    }

    private String typeName(Room room) {

        if (room instanceof SingleRoom) return "Single";
        if (room instanceof DoubleRoom) return "Double";
        if (room instanceof SuiteRoom) return "Suite";
        if (room instanceof VIPRoom) return "VIP";

        return "Unknown";
    }

    private Class<? extends Room> typeClass(String type) {

        return switch (type) {
            case "Single" -> SingleRoom.class;
            case "Double" -> DoubleRoom.class;
            case "Suite" -> SuiteRoom.class;
            case "VIP" -> VIPRoom.class;
            default -> null;
        };
    }

    private boolean validDates() {

        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();

        if (in == null || out == null
                || in.isBefore(LocalDate.now())
                || !out.isAfter(in)) {

            new Alert(
                    Alert.AlertType.WARNING,
                    "Select valid check-in and check-out dates."
            ).showAndWait();

            return false;
        }

        return true;
    }

    @FXML
    private void findMatches() {

        if (!validDates()) return;

        searchButton.setDisable(true);
        statusLabel.setText("Checking availability...");

        String selected = roomTypeCombo.getValue();
        int guests = guestsCombo.getValue();

        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();

        Task<List<Room>> task = new Task<>() {

            @Override
            protected List<Room> call() {

                List<Room> matches = new ArrayList<>();

                List<String> types =
                        "All Types".equals(selected)
                                ? List.of(
                                "Single",
                                "Double",
                                "Suite",
                                "VIP"
                        )
                                : List.of(selected);

                for (String type : types) {

                    RoomSearchFilter filter =
                            new RoomSearchFilter(
                                    typeClass(type),
                                    null,
                                    BigDecimal.ZERO,
                                    MAX_PRICE,
                                    guests
                            );

                    Room match = roomManager.findMatchingRoom(
                            filter, in, out
                    );

                    if (match != null) {
                        matches.add(match);
                    }
                }

                return matches;
            }
        };

        task.setOnSucceeded(event -> {

            searchButton.setDisable(false);

            List<Room> matches = task.getValue();

            statusLabel.setText(
                    matches.size()
                            + " matching room type(s) available."
            );

            displayRooms(matches);
        });

        task.setOnFailed(event -> {

            searchButton.setDisable(false);
            statusLabel.setText("Availability check failed.");

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task, "room-matching");
        thread.setDaemon(true);
        thread.start();
    }

    private void reserveType(Room browsedRoom) {

        String type = typeName(browsedRoom);

        // Automatically select the browsed room type.
        roomTypeCombo.setValue(type);

        if (!validDates()) return;

        int guests = guestsCombo.getValue();

        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();

        statusLabel.setText(
                "Finding an available " + type + " room..."
        );

        Task<Room> task = new Task<>() {

            @Override
            protected Room call() {

                RoomSearchFilter filter =
                        new RoomSearchFilter(
                                typeClass(type),
                                null,
                                BigDecimal.ZERO,
                                MAX_PRICE,
                                guests
                        );

                return roomManager.findMatchingRoom(
                        filter, in, out
                );
            }
        };

        task.setOnSucceeded(event -> {

            Room matched = task.getValue();

            if (matched == null) {

                new Alert(
                        Alert.AlertType.INFORMATION,
                        "No " + type
                                + " room is available for these dates."
                ).showAndWait();

                return;
            }

            Alert preview = new Alert(Alert.AlertType.INFORMATION);

            preview.setTitle("Booking Details");
            preview.setHeaderText(type + " Room - Available");

            preview.setContentText(
                    "Check-in: " + in
                            + "\nCheck-out: " + out
                            + "\nGuests: " + guests
                            + "\nMatched Room ID: "
                            + matched.getRoomId()
                            + "\nBase Price: "
                            + matched.getBasePrice()
                            + " EGP/night"
                            + "\n\nNo reservation has been saved yet."
            );

            preview.showAndWait();
        });

        task.setOnFailed(event -> {

            new Alert(
                    Alert.AlertType.ERROR,
                    "Could not check room availability."
            ).showAndWait();

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task, "reserve-match");
        thread.setDaemon(true);
        thread.start();
    }
}
