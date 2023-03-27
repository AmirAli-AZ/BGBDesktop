package com.bgbrowser.extensionframework;

public interface BGBApplication {

    void initialize(Browser browser);

    void onDestroyed();
}
