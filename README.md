# Git
This is a VCS with CLI which can be used for storing different versions of text files similar to GIT. 
## Set-up
How to launch the console application:
1. Put the script gitCli.sh into the folder which will later become a git-repository
2. Change path in gitCli.sh from /home/mario/Desktop/tmp/java-1-hse-2021/ to your project directory path
3. Launch application from the command line: `./gitCli.sh init`
`./gitCli.sh add file1.txt file2.txt`
## Usage
This vcs supports following commands:
* `help` -- list all commands
* `init` -- initiate a new repository
* `add <files>` -- add individual file or collection of files
* `rm <files>` -- remove individual file or collection of files from the repository
* `status` -- show status of files (e.g. modified/removed/added)
* `commit <message>` -- make a new commit with message and current date and time
* `reset <to_revision>` -- similar to `git reset --hard`
* `log [from_revision]`-- show history from the revision
* `checkout <revision>`
    * Possible values of `revision`:
        * `commit hash` -- hash of the commit
        * `master` -- branch name
        * `HEAD~N`, where `N` is a non-negative number. `HEAD~N` gets Nth commit before HEAD (`HEAD~0 == HEAD`)
* `checkout -- <files>` -- reset all changes in files
* `branch-create <branch>` -- create a new branch called `<branch>`
* `branch-remove <branch>` -- delete the branch called `<branch>`
* `show-branches` -- show all branches

## Note
* `<smth>` means that arguments are required
* `[smth]` means that arguments are optional
* The full copy of repository IS NOT stored for each revision, so this VCS does not have unnecessary memory usage
