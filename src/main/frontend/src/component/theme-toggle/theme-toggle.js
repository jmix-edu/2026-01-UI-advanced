import {html, LitElement} from 'lit';
import {PolylitMixin} from '@vaadin/component-base/src/polylit-mixin.js';
import {defineCustomElement} from '@vaadin/component-base/src/define.js';
import {ElementMixin} from '@vaadin/component-base/src/element-mixin.js';
import {TooltipController} from '@vaadin/component-base/src/tooltip-controller.js';
import {css, ThemableMixin} from '@vaadin/vaadin-themable-mixin/vaadin-themable-mixin.js';
import {buttonStyles} from '@vaadin/button/src/vaadin-button-core-styles.js';
import {button as buttonLumoStyles} from '@vaadin/button/theme/lumo/vaadin-button-styles.js';
import {ButtonMixin} from '@vaadin/button/src/vaadin-button-mixin.js';

const themeToggleStyles = css`
    :host {
        background: transparent;
        color: var(--lumo-text-color);
        min-width: var(--lumo-button-size);
        padding-left: calc(var(--lumo-button-size) / 4);
        padding-right: calc(var(--lumo-button-size) / 4);
    }
`;

class ThemeToggle extends ButtonMixin(
    ElementMixin(ThemableMixin(PolylitMixin(LitElement)))
) {

    static get is() {
        return 'theme-toggle';
    }

    static get properties() {
        return {
            ariaLabel: {
                type: String,
                attribute: 'aria-label',
                reflect: true
            },
            storageKey: {
                type: String
            }
        };
    }

    static get styles() {
        // base Vaadin button styles + Lumo styles + our own
        return [buttonStyles, buttonLumoStyles, themeToggleStyles];
    }

    constructor() {
        super();
        this.ariaLabel = 'Theme toggle';
        this.storageKey = 'jmix.flowui.theme';

        this._onClick = this._onClick.bind(this);
    }

    connectedCallback() {
        super.connectedCallback();
        this.addEventListener('click', this._onClick);
    }

    disconnectedCallback() {
        this.removeEventListener('click', this._onClick);
        super.disconnectedCallback();
    }

    /** @protected */
    firstUpdated(_changedProperties) {
        // Tooltip controller (matches Vaadin components behavior)
        this._tooltipController = new TooltipController(this);
        this.addController(this._tooltipController);

        // Apply stored theme on first render
        this.applyStorageTheme();
    }

    render() {
        return html`
            <div class="vaadin-button-container">
                <span part="prefix" aria-hidden="true">
                    <slot name="prefix"></slot>
                </span>
                <span part="label">
                    <slot></slot>
                </span>
            </div>

            <slot name="tooltip"></slot>
        `;
    }

    updated(changedProps) {
        if (changedProps.has('storageKey')) {
            const oldKey = changedProps.get('storageKey');
            if (oldKey) {
                const theme = localStorage.getItem(oldKey);
                localStorage.removeItem(oldKey);
                if (theme) {
                    localStorage.setItem(this.storageKey, theme);
                }
            }
        }
    }

    _onClick() {
        this.toggleTheme();
    }

    applyStorageTheme() {
        const storageTheme = this.getStorageTheme();
        const currentTheme = this.getCurrentTheme();
        if (storageTheme && currentTheme !== storageTheme) {
            this.applyTheme(storageTheme);
        }
    }

    getStorageTheme() {
        return localStorage.getItem(this.storageKey);
    }

    getCurrentTheme() {
        return document.documentElement.getAttribute('theme');
    }

    toggleTheme() {
        const theme = this.getCurrentTheme();
        this.applyTheme(theme === 'dark' ? '' : 'dark');
    }

    applyTheme(theme) {
        document.documentElement.setAttribute('theme', theme);
        localStorage.setItem(this.storageKey, theme);

        const customEvent = new CustomEvent('theme-changed', {
            detail: {value: theme},
            bubbles: true,
            composed: true
        });
        this.dispatchEvent(customEvent);
    }
}

defineCustomElement(ThemeToggle);

export {ThemeToggle};