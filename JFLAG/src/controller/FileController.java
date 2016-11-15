package controller;

import java.io.IOException;

/**
 * @author Ritwik Banerjee
 */
public interface FileController {

    void handleNewRequest();

    void handleSaveRequest() throws IOException;

    void handleExitRequest();

	void handleLoginRequest();
}
