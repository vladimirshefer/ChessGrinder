import React from "react";
import {Md5} from "ts-md5";

/**
 * @see https://gravatar.com/site/implement/images/
 * @param text the source text. usually email address. the image is generated based on this text.
 * @param alt
 * @param className
 * @param size
 * @param type
 * @constructor
 */
export default function Gravatar(
    {
        text,
        alt = "",
        className = "",
        size,
        type
    }: {
        text: string,
        alt?: string,
        className?: string,
        size?: number,
        type: GravatarType
    }
) {
    return <img
        className={className}
        alt={alt}
        src={`https://gravatar.com/avatar/${Md5.hashStr(text)}?d=${type}${size ? `&s=${size}` : ""}`}
    />
}

export enum GravatarType {
    /**
     * do not load any image if none is associated with the email hash,
     * instead return an HTTP 404 (File Not Found) response
     */
    NotFound = "404",
    /**
     * a simple, cartoon-style silhouetted outline of a person (does not vary by email hash)
     */
    MysteryPerson = "mp",
    /**
     * a geometric pattern based on an email hash
     */
    Identicon = "identicon",
    /**
     * a generated 'monster' with different colors, faces, etc
     */
    MosterId = "monsterid",
    /**
     * generated faces with differing features and backgrounds
     */
    Wavatar = "wavatar",

    /**
     * awesome generated, 8-bit arcade-style pixelated faces
     */
    Retro = "retro",
    /**
     * a generated robot with different colors, faces, etc
     */
    Robohash = "robohash",
    /**
     * a transparent PNG image (border added to HTML below for demonstration purposes)
     */
    Blank = "blank",
}
