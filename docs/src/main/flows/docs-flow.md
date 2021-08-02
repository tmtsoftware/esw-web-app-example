# Adding Paradox Documentation

If you want to write documentation for your application using [paradox](https://developer.lightbend.com/docs/paradox/current/getting-started.html), you can follow below steps.

## Pre-requisite
Your application code is checked in some repo in your organization github account.

## Documentation development flow

Generated sample application contains a `docs` folder, which you can use to write documentation about your project.
Top level `build.sbt` contains the setup for this `docs` sbt project.

Launch sbt from your console and run command `makeSite`. Then run command `openSite`
```bash
sbt
sbt:sample>makeSite
sbt:sample>openSite
```

It will generate sample documentation and open it in your browser showing sample page.
You can modify various `.md` files present in `docs` folder and repeat above steps and verify your generated documentation.

## Documentation publish flow

When your documentation is ready to be published, go to top level `build.sbt`, it contains a variable with name `githubRepoUrl`.
Update this url with your github repo url.
Paradox uses a special branch `gh-pages`, in your github repo itself to keep track of your documentation, Hence we need to set this github repo url.

Reload sbt so that updated url is available in sbt.
```bash
sbt:sample>reload
```

Create `gh_pages` branch in your repo following these [quick steps](https://github.com/sbt/sbt-ghpages#initializing-the-gh-pages-branch).
Very that you see a new branch `gh_pages` in your github repo with an empty commit.

Run command `ghpagesPushSite`, to publish your documentation.

```bash
sbt:sample>ghpagesPushSite
```

Very that you see a new commit in branch `gh_pages`, and branch should contain a folder with name `0.1.0-SNAPSHOT`

Open your published site using URL, `http://{your-username}.github.io/{your-project}/0.1.0-SNAPSHOT/`
