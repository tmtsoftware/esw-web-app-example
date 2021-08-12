# Adding Paradox Documentation

The section of the tutorial describes how to write documentation for your application using [paradox](https://developer.lightbend.com/docs/paradox/current/getting-started.html).

## Pre-requisite

Your application code is checked in some repo in your organization Github account.

## Documentation development flow

The sample application generated from the template contains a `docs` folder. 
This is where you will write the documentation files for your project.
The top-level `build.sbt` file contains the setup for this `docs` sbt project.

Launch sbt from your console and run command `makeSite`. Then run the command `openSite`

```bash
sbt
sbt:sample> makeSite
sbt:sample> openSite
```

It will generate sample documentation and open it in your browser showing sample page.
You can modify various `.md` files present in `docs` folder and repeat above steps and verify your generated documentation.

## Documentation publish flow

The template sets your project up to publish your documentation to Github using [Github Pages](https://pages.github.com/).
You need to specify the URL of your Github repo in the variable `githubRepoUrl` in your top-level `build.sbt` file.
This allows paradox to use a special branch, `gh-pages`, in your Github repo to keep track of your documentation.

Reload sbt so that updated URL is available in sbt.

```bash
sbt:sample> reload
```

Create the `gh_pages` branch in your repo following these [quick steps](https://github.com/sbt/sbt-ghpages#initializing-the-gh-pages-branch).
Verify that you see a new branch `gh_pages` in your github repo with an empty commit.

Then, you can run the command `ghpagesPushSite`, to publish your documentation.

```bash
sbt:sample> ghpagesPushSite
```

Verify that you see a new commit in branch `gh_pages`, and branch should contain a folder with name `0.1.0-SNAPSHOT`

Open your published site using URL, `http://{your-username}.github.io/{your-project}/0.1.0-SNAPSHOT/`
