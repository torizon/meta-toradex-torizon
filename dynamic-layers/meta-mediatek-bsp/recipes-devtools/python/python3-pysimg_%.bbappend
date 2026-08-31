# Upstream github.com/dlenski/PySIMG renamed its default branch master -> main
# and deleted master, breaking the vendor recipe's branch=master fetch. The
# pinned SRCREV is still on main, so only retarget the branch (in place, leaving
# the rest of SRC_URI untouched).
python () {
    d.setVar('SRC_URI', d.getVar('SRC_URI').replace('branch=master', 'branch=main'))
}
