import Vue from "vue";
export default Vue.extend({
    name: "AlgoliaSearchFull",
    props: {
        options: { type: Object, required: true },
    },
    watch: {
        $lang(newValue) {
            this.update(this.options, newValue);
        },
        options(newValue) {
            this.update(newValue, this.$lang);
        },
    },
    mounted() {
        this.initialize(this.options, this.$lang);
    },
    methods: {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        initialize(userOptions, _lang) {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            void import(/* webpackChunkName: "docsearch" */ "@docsearch/js").then((docsearch) => {
                // eslint-disable-next-line
                docsearch.default({
                    container: "#docsearch",
                    placeholder: this.$site.themeConfig.searchPlaceholder || "",
                    ...userOptions,
                    searchParameters: userOptions.searchParameters || {},
                    transformItems: (items) => items.map((item) => ({
                        ...item,
                        url: this.getRelativePath(item.url),
                    })),
                    hitComponent: ({ hit, children }) => ({
                        type: "a",
                        key: null,
                        ref: undefined,
                        constructor: undefined,
                        props: {
                            href: hit.url,
                            onClick: (event) => {
                                // We rely on the native link scrolling when user is
                                // already on the right anchor because Vue Router doesn’t
                                // support duplicated history entries.
                                if (this.$route.fullPath === hit.url)
                                    return;
                                const { pathname: hitPathname } = new URL(`${window.location.origin}${hit.url}`);
                                // If the hits goes to another page, we prevent the native link behavior
                                // to leverage the Vue Router loading feature.
                                if (this.$route.path !== hitPathname)
                                    event.preventDefault();
                                void this.$router.push(hit.url);
                            },
                            children,
                        },
                    }),
                    navigator: {
                        navigate: ({ suggestionUrl }) => {
                            const { pathname: hitPathname } = new URL(window.location.origin + suggestionUrl);
                            // Vue Router doesn’t handle same-page navigation so we use
                            // the native browser location API for anchor navigation.
                            if (this.$route.path === hitPathname)
                                window.location.assign(window.location.origin + suggestionUrl);
                            else
                                void this.$router.push(suggestionUrl);
                        },
                        navigateNewTab({ suggestionUrl }) {
                            window.open(suggestionUrl);
                        },
                        navigateNewWindow({ suggestionUrl }) {
                            window.open(suggestionUrl);
                        },
                    },
                });
            });
        },
        getRelativePath(absoluteUrl) {
            const { pathname, hash } = new URL(absoluteUrl);
            const url = pathname.replace(this.$site.base, "/") + hash;
            return url;
        },
        update(options, lang) {
            this.$el.innerHTML = '<div id="docsearch"></div>';
            this.initialize(options, lang);
        },
    },
});
//# sourceMappingURL=Full.js.map