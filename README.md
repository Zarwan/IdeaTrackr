# IdeaTrackr
An Android app to track ideas on the go

Currently able to create, edit, and delete your own ideas. You can also login with your Google account and have the option to backup your ideas into Google Drive. Right now the backups are formatted text files with the timestamp
of the backup in the name.

The UI is pretty basic (sucky) right now.

Potential Enhancements (for anyone looking to contribute, or me if I get bored):
- Improve Drive integration (Will probably have to migrate to the Drive REST API; currently using the Drive API for Android)
    -> Share ideas with a friend on Drive
    -> Update a single Drive file instead of making a new one for each backup, while renaming it with the most recent timestamp
    -> Provide a sync option where the ideas in the app are updated based on a file in Drive
        -> This could be used to have multiple "lists" of ideas, depending on which one you load (ex. one for yourself, one for your team that's working on a project)
- Improve the terrible UI

