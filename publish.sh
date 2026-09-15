V=v0.1.0
#git tag -d $V || true
#git push origin :refs/tags/$V || true
git tag $V
git push origin $V
