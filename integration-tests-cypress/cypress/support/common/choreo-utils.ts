export const appNamePrefix = 'a' + Date.now();
export const marketplaceText = 'marketplace';
export const integrationsText = 'integrations';
export const servicesText = 'services';
export const APIsText = 'apis';
export const devOpsText = 'devops';

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
