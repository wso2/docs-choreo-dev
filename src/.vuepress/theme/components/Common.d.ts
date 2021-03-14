import GlobalEncryptMixin from "@theme/util/globalEncryptMixin";
declare const Common_base: import("vue-class-component/lib/declarations").VueClass<GlobalEncryptMixin>;
export default class Common extends Common_base {
    private readonly navbar;
    private readonly sidebar;
    private isSidebarOpen;
    private hideNavbar;
    private touchStart;
    private get enableNavbar();
    private get enableSidebar();
    private get sidebarItems();
    private get pageClasses();
    private get headers();
    private get enableAnchor();
    /** Get scroll distance */
    private getScrollTop;
    protected mounted(): void;
    private toggleSidebar;
    private onTouchStart;
    private onTouchEnd;
    private getHeader;
}
export {};
