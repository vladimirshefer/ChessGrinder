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
export function Gravatar(
    {
        text,
        alt = "",
        className = "",
        size,
        type,
        inputType = "TEXT",
    }: {
        text: string | undefined,
        alt?: string,
        className?: string,
        size?: number,
        type: GravatarType,
        inputType?: "TEXT" | "MD5",
    }
) {
    let hash = inputType === "MD5" && !!text ? text : Md5.hashStr(text || "");
    return <img
        className={className}
        alt={alt}
        src={`https://gravatar.com/avatar/${hash}?d=${type}${size ? `&s=${size}` : ""}`}
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

export function UserAvatarImg(
    {
        emailHash,
        className = "",
        size,
    }: {
        emailHash: string | undefined,
        className?: string,
        size?: number,
    }
) {
    return <Gravatar
        text={emailHash}
        alt={"User avatar"}
        className={className}
        size={size}
        type={GravatarType.Robohash}
        inputType={"MD5"}
    />;
}

export function EmptyUserAvatarImg(
    {
        className = "",
        size,
    }: {
        className?: string,
        size?: number,
    }
) {
    return <Gravatar
        text={""}
        alt={"Anonymous user avatar"}
        className={className}
        size={size}
        type={GravatarType.MysteryPerson}
        inputType={"MD5"}
    />;
}

export function BadgeIcon(
    {
        title,
        className = "",
        size,
    }: {
        title: string,
        className?: string | undefined,
        size: number,
    }
) {
    return <Gravatar
        text={title}
        type={GravatarType.Identicon}
        size={size}
        className={"rounded-full " + className}
    />
}
