/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_CAPTCHA_PUBLIC_KEY?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

