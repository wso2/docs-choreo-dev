"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.eject = void 0;
const chalk_1 = require("chalk");
const fs_extra_1 = require("fs-extra");
const path_1 = require("path");
const EXCLUDED_FILES = [
    "__tests__",
    ".npmignore",
    "test",
    "LICENSE",
    "package.json",
    "node_modules",
    "README.md",
    "readme.md",
];
const eject = async (dir) => {
    try {
        const sourceDir = path_1.resolve(__dirname, "../");
        const targetDir = path_1.resolve(path_1.resolve(dir), ".vuepress/theme");
        await fs_extra_1.copy(sourceDir, targetDir, {
            filter: (src) => {
                return !EXCLUDED_FILES.includes(path_1.relative(sourceDir, src));
            },
        });
        console.log(`Copied vuepress-theme-hope into ${chalk_1.cyan(targetDir)}.\n`);
    }
    catch (err) {
        console.error(chalk_1.red(err.stack || ""));
        process.exitCode = 1;
    }
};
exports.eject = eject;
//# sourceMappingURL=eject.js.map