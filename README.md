# JGitStats

Yet another program for displaying stats about a Git repository.

Goals:

1. Lines of code per given period, filterable by file type
2. Most lines of code per day of week, hour of day, etc
3. Follows renames for lines of code
4. Exclude certain files/directories

Ideas:

1. Dynamic/static mode. Query-able API to return JSON so graphs can be done
   client-side.

2. In the global diff page, allow user to see all the files in the repository,
   with the number of changes to the files. When you click on that file's page,
   you can see all the diff +/-s, maybe even the file at that revision? You'd
   be able to see the renames, too!

3. A way to see files that are no longer in the project?

4. Characterize the project's commits

5. Which % of the code is currently owned by who? Git blame, keep track of all
   every line.

TODOs:

1. Per-user +/- contributions. Can we intelligently detect author renames?
   If I am `Alex Selesse <selesse@gmail.com>` then I become
   `Alex Selesse <alex@selesse.com>` and I never commit from
   `selesse@gmail.com` again, can I detect that programmatically?


2. Average commit length
