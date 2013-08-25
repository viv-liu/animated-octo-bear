
/**Play_UI
*
* Upon selecting play this functions similar to the Import_UI
* except only specifically looks for files in the project folder
* and then displays a much better looking UI for the users to
* select the song they wish to play which then calls Play_process
* to start playing the game.
*
*/

/**
* Called by: title_screen
* Calls: Play_process, File_Process
*
* Input of:
* User OnClick
*
* Output of:
* Intent to Play_process with file location
*
*
* @author Shen Wang
*
*/


package UI.module;

public class Play_UI {

/**Two Fragments
*
*1: ListFrag
*2: DetFrag
*
*ListFrag:
*Located on right side of screen.
*Takes each list item using File_Process.
*Places it in a list format with some stylized graphics.
*
*User Actions:
*OnFling or OnPress hold then drag to navigate list up and down. (only if the user was on right side)
*Add in bounce effect animation so it can scroll beyond bottom of list and bouces back up
*
*
*DetFrag:
*Located on left side of screen
*Looks at the song's file using _(no current known class we have)_ and finds it's metadata.
*Then displays the album art + song info (such as length of song and etc...) similar to most music apps
*
*/

/**Two Buttons
*
*1: Home
*2: Play
*
*Home:
*Located on top left side of screen (Small button)
*OnClick go back to title screen
*
*
*Play:
*Located on bottom left side of screen (Big button)
*OnClick pass intent of currently selected song to Play_process
*
*/

}
