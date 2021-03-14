import Vue from "vue";
import { groupSidebarHeaders } from "@theme/util/sidebar";
import { isActive } from "@theme/util/path";
const renderLink = (h, { text, link, active }) => h("RouterLink", {
    props: {
        to: link,
        activeClass: "",
        exactActiveClass: "",
    },
    class: {
        active,
        "anchor-link": true,
    },
}, [h("div", {}, [text])]);
const renderChildren = (h, { children, path, route, maxDepth, depth = 2 }) => {
    if (!children || depth > maxDepth)
        return null;
    return h("ul", { class: "anchor-list" }, children.map((child) => {
        const active = isActive(route, `${path}#${child.slug}`);
        return h("li", { class: ["anchor", `anchor${depth}`] }, [
            renderLink(h, {
                text: child.title,
                link: `${path}#${child.slug}`,
                active,
            }),
            renderChildren(h, {
                children: child.children || false,
                path,
                route,
                maxDepth,
                depth: depth + 1,
            }),
        ]);
    }));
};
export default Vue.extend({
    name: "Anchor",
    functional: true,
    props: {
        header: {
            type: Array,
            default: () => [],
        },
    },
    render(h, { parent: { $page, $route, $themeConfig, $themeLocaleConfig }, props }) {
        const { header } = props;
        const maxDepth = ($page.frontmatter.sidebarDepth ||
            $themeLocaleConfig.sidebarDepth ||
            $themeConfig.sidebarDepth ||
            2) + 1;
        const children = groupSidebarHeaders(header);
        return h("div", { attrs: { class: "anchor-place-holder" } }, [
            h("aside", { attrs: { id: "anchor" } }, [
                h("div", { class: "anchor-wrapper" }, [
                    renderChildren(h, {
                        children,
                        path: $route.path,
                        route: $route,
                        maxDepth,
                    }),
                ]),
            ]),
        ]);
    },
});
//# sourceMappingURL=Anchor.js.map