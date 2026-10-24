import React from "react";

/**
 * Very basic and safe markdown renderer.
 * Supported: **bold**, *italic*, _italic_, `code`, [text](https://link), "- item" lists, paragraphs and line breaks.
 * It never produces raw HTML (no dangerouslySetInnerHTML), so the input can't inject any markup or scripts.
 * Only http, https and mailto links are rendered as links.
 */

const INLINE = /\*\*(\S(?:.*?\S)?)\*\*|\*(\S(?:.*?\S)?)\*|_(\S(?:.*?\S)?)_|`([^`]+)`|\[([^\]]+)\]\(([^)\s]+)\)/;
const LIST_ITEM = /^\s*[-*]\s+(.*)$/;

export function isSafeUrl(url: string): boolean {
    return /^(https?:\/\/|mailto:)/i.test(url.trim());
}

function renderInline(text: string, allowLinks: boolean = true): React.ReactNode[] {
    const nodes: React.ReactNode[] = [];
    let rest = text;
    let key = 0;
    while (rest) {
        const m = INLINE.exec(rest);
        if (!m) {
            nodes.push(rest);
            break;
        }
        if (m.index > 0) {
            nodes.push(rest.slice(0, m.index));
        }
        if (m[3] !== undefined && m.index > 0 && /\w/.test(rest[m.index - 1])) {
            // underscore inside a word, like snake_case_name, is not an italic
            nodes.push("_");
            rest = rest.slice(m.index + 1);
            continue;
        }
        if (m[1] !== undefined) {
            nodes.push(<strong key={key++}>{renderInline(m[1], allowLinks)}</strong>);
        } else if (m[2] !== undefined) {
            nodes.push(<em key={key++}>{renderInline(m[2], allowLinks)}</em>);
        } else if (m[3] !== undefined) {
            nodes.push(<em key={key++}>{renderInline(m[3], allowLinks)}</em>);
        } else if (m[4] !== undefined) {
            nodes.push(<code key={key++}>{m[4]}</code>);
        } else if (allowLinks && isSafeUrl(m[6])) {
            nodes.push(
                <a key={key++} href={m[6].trim()} target={"_blank"} rel={"noopener noreferrer nofollow ugc"}
                   className={"underline"}>
                    {renderInline(m[5], false)}
                </a>
            );
        } else {
            nodes.push(m[0]);
        }
        rest = rest.slice(m.index + m[0].length);
    }
    return nodes;
}

function renderBlocks(source: string): React.ReactNode[] {
    const lines = source.replace(/\r\n?/g, "\n").split("\n");
    const blocks: React.ReactNode[] = [];
    let paragraph: string[] = [];
    let list: string[] = [];

    const flushParagraph = () => {
        if (paragraph.length === 0) return;
        blocks.push(
            <p key={blocks.length}>
                {paragraph.map((line, idx) => <React.Fragment key={idx}>
                    {idx > 0 && <br/>}
                    {renderInline(line)}
                </React.Fragment>)}
            </p>
        );
        paragraph = [];
    };
    const flushList = () => {
        if (list.length === 0) return;
        blocks.push(
            <ul key={blocks.length} className={"list-disc list-inside"}>
                {list.map((item, idx) => <li key={idx}>{renderInline(item)}</li>)}
            </ul>
        );
        list = [];
    };

    for (const line of lines) {
        const item = LIST_ITEM.exec(line);
        if (item) {
            flushParagraph();
            list.push(item[1]);
        } else if (line.trim() === "") {
            flushParagraph();
            flushList();
        } else {
            flushList();
            paragraph.push(line);
        }
    }
    flushParagraph();
    flushList();
    return blocks;
}

export default function Markdown({source, className}: { source?: string | null, className?: string }) {
    if (!source || !source.trim()) {
        return null;
    }
    return <div className={`grid gap-2 break-words ${className || ""}`}>{renderBlocks(source)}</div>;
}
