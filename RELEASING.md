
# Release steps

1. Make sure `esw-web-app-template.g8` is green after following its release steps
2. update `ESW` & `CSW` related dependency versions in `Libs.scala` of `backend` project 
3. update `sbt-docs` versions in top level `plugins.sbt` used by `docs` project
4. update `esw-ts` versions in `package.json` of `frontend` project
5. Run `npm install` in `frontend` project, so that `package-lock.json` also gets updated version.
6. Commit and push your changes
7. create the appropriate release tag using git and push the tag e.g. `git tag v0.1.0-RC1` and `git push origin v0.1.0-RC1`
