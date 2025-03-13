package core;

public class Field {
    private static final int SIZE = 8;
    private Tile[][] field;
    public boolean whiteTurn;

    public Field() {
        field = new Tile[SIZE][SIZE];
        whiteTurn = true;
        createField();
        //createTestField();
    }

    // Возвращает поле
    public Tile[][] getField() {
        return field;
    }

    // Возвращает сторону хода
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    // Смена хода
    public void switchTurn() {
        whiteTurn = !whiteTurn;
    }

    // Создание поля
    public void createField() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    field[row][col] = new Tile(TileState.EMPTY);
                } else if (row < 3) {
                    field[row][col] = new Man(TileState.BLACK);
                } else if (row > 4) {
                    field[row][col] = new Man(TileState.WHITE);
                } else {
                    field[row][col] = new Tile(TileState.EMPTY);
                }
            }
        }
    }
    public void createTestField() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                field[i][j] = new Tile(TileState.EMPTY);
            }
        }
        //field[3][4] = new Man(TileState.BLACK_KING);
        field[5][6] = new Man(TileState.WHITE);
        field[4][5] = new Man(TileState.BLACK);
        //field[6][1] = new Man(TileState.WHITE);
    }

    // Перемещение шашки
    public boolean move(int fromRow, int fromCol, int toRow, int toCol) {
        if (hasAnyCaptureMove()) {
            if (Math.abs(toRow - fromRow) != 2 && !isKing(fromRow, fromCol)) {
                System.out.println("You must capture a piece if possible!");
                return false;
            }
            if (!isChecker(fromRow, fromCol) && !isValidKingMove(fromRow, fromCol, toRow, toCol)) {
                System.out.println("The king must capture a piece!");
                return false;
            }
        }

        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        if (Math.abs(toRow - fromRow) == 2) {
            if (isChecker(fromRow, fromCol)) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                Tile midTile = field[midRow][midCol];

                if (isOpponentTile(midTile)) {
                    field[midRow][midCol] = new Tile(TileState.EMPTY);
                } else {
                    return false;
                }
            }
        }

        field[toRow][toCol] = field[fromRow][fromCol];
        field[fromRow][fromCol] = new Tile(TileState.EMPTY);

        checkKing(fromRow, fromCol, toRow, toCol);

        return true;
    }

    // Проверка на валидность хода
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isWithinBounds(fromRow, fromCol) || !isWithinBounds(toRow, toCol) ||
                field[fromRow][fromCol].isEmpty() || field[toRow][toCol].isNotEmpty())
        {
            System.out.println("Error in isValidMove.");
            return false;
        }

        return isChecker(fromRow, fromCol) ?
                isValidManMove(fromRow, fromCol, toRow, toCol) :
                isValidKingMove(fromRow, fromCol, toRow, toCol);
    }

    // Обычные Шашки
    public boolean isValidManMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDelta = toRow - fromRow;
        int colDelta = Math.abs(toCol - fromCol);
        int direction = (field[fromRow][fromCol].getState() == TileState.WHITE) ? -1 : 1;

        if (rowDelta == direction && colDelta == 1) {
            return true;
        } else if (rowDelta == 2 * direction && colDelta == 2) {
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            return isOpponentTile(field[midRow][midCol]);
        }

        return false;
    }

    // Дамки
    public boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        return isPathClear(fromRow, fromCol, toRow, toCol, false)
                || isPathClear(fromRow, fromCol, toRow, toCol, true);
    }

    // Проверка на пустоту диагонали или наличие шашки противника
    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol, boolean checkForOpponent) {
        int rowStep = (toRow > fromRow) ? 1 : -1;
        int colStep = (toCol > fromCol) ? 1 : -1;
        int row = fromRow + rowStep;
        int col = fromCol + colStep;
        boolean foundOpponent = false;

        while (row != toRow && col != toCol) {
            if (field[row][col].isNotEmpty()) {
                if (checkForOpponent && isOpponentTile(field[row][col]) && !foundOpponent) {
                    foundOpponent = true;
                } else {
                    return false;
                }
            }
            row += rowStep;
            col += colStep;
        }

        return !checkForOpponent || foundOpponent;
    }

    // Проверка на выход за границы поля
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // Превращение в Короля
    public void checkKing(int fromRow, int fromCol, int toRow, int toCol) {
        if (field[fromRow][fromCol].getState() == TileState.WHITE_KING || field[fromRow][fromCol].getState() == TileState.BLACK_KING) {
            return;
        }
        if (toRow == 0 && field[toRow][toCol].getState() == TileState.WHITE) {
            field[toRow][toCol] = new King(TileState.WHITE_KING);
        }
        if (toRow == 7 && field[toRow][toCol].getState() == TileState.BLACK) {
            field[toRow][toCol] = new King(TileState.BLACK_KING);
        }
    }

    // Проверка на противника
    public boolean isOpponentTile(Tile tile) {
        return (whiteTurn && (tile.getState() == TileState.BLACK || tile.getState() == TileState.BLACK_KING)) ||
                (!whiteTurn && (tile.getState() == TileState.WHITE || tile.getState() == TileState.WHITE_KING));
    }

    // Проверка на обычную Шашку
    public boolean isChecker(int row, int col) {
        return field[row][col].getState() == TileState.WHITE || field[row][col].getState() == TileState.BLACK;
    }

    // Проверка на Короля
    public boolean isKing(int row, int col) {
        return field[row][col].getState() == TileState.WHITE_KING || field[row][col].getState() == TileState.BLACK_KING;
    }

    // Проверка на возможность хода со взятием
    private boolean hasAnyCaptureMove() {
        int[][] directions = {{-1, 1}, {-1, -1}, {1, -1}, {1, 1}};

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = field[row][col];

                if (tile.isEmpty() || isOpponentTile(tile)) {
                    continue;
                }

                for (int[] dir : directions) {
                    int toRow = row + 2 * dir[0];
                    int toCol = col + 2 * dir[1];
                    int midRow = row + dir[0];
                    int midCol = col + dir[1];

                    if (isWithinBounds(toRow, toCol) && field[toRow][toCol].isEmpty()) {
                        if (isOpponentTile(field[midRow][midCol])) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Проверка на конец игры
    public boolean endGame() {
        boolean whiteLeft = false;
        boolean blackLeft = false;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                TileState state = field[i][j].getState();
                if (state == TileState.WHITE || state == TileState.WHITE_KING) {
                    whiteLeft = true;
                } else if (state == TileState.BLACK || state == TileState.BLACK_KING) {
                    blackLeft = true;
                }

                if (whiteLeft && blackLeft) {
                    return false;
                }
            }
        }
        return true;
    }
}