export const appNamePrefix = 'a' + Date.now();

/**
 * Create name for app.
 *
 * @returns true name for a new app
 */
export const generateAppName = (name: string) => {
    return appNamePrefix + "-" + name;
}
  
/**
 * Create name for api.
 *
 * @returns true name for a new api
 */
export const generateApiName = (name: string) => {
    return appNamePrefix + name;
}

export const normalizeText = (s: string) => {
    return s.replace(/\s+/g, '\u00a0')
}
