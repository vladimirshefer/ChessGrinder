import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';
import {expect, test} from 'vitest';
import Markdown from './Markdown';

function html(source: string): string {
    return renderToStaticMarkup(<Markdown source={source}/>);
}

test('renders bold, italic and code', () => {
    const result = html("**bold** *it* _it2_ `code`");
    expect(result).toContain("<strong>bold</strong>");
    expect(result).toContain("<em>it</em>");
    expect(result).toContain("<em>it2</em>");
    expect(result).toContain("<code>code</code>");
});

test('renders safe links with rel and target', () => {
    const result = html("[Club](https://chessgrinder.com/a?b=1)");
    expect(result).toContain('href="https://chessgrinder.com/a?b=1"');
    expect(result).toContain('target="_blank"');
    expect(result).toContain("noopener");
});

test('does not render unsafe link schemes', () => {
    const result = html("[x](javascript:alert(1)) [y](data:text/html,hi)");
    expect(result).not.toContain("<a ");
    expect(result).not.toContain("href=");
});

test('escapes html', () => {
    const result = html("<script>alert('x')</script><img src=x onerror=alert(1)>");
    expect(result).not.toContain("<script");
    expect(result).not.toContain("<img");
    expect(result).toContain("&lt;script&gt;");
});

test('renders paragraphs, line breaks and lists', () => {
    const result = html("line1\nline2\n\n- a\n- b\n\nlast");
    expect(result).toContain("line1<br/>line2");
    expect(result).toContain("<li>a</li><li>b</li>");
    expect(result).toContain("<p>last</p>");
});

test('keeps snake_case and math stars as plain text', () => {
    const result = html("my_nick_name 2 * 3 * 4");
    expect(result).not.toContain("<em>");
});

test('renders nothing for empty source', () => {
    expect(html("  ")).toBe("");
});
