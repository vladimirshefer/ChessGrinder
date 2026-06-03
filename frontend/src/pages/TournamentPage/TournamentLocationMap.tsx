const CITY_MAP_EMBED_URLS: Record<string, string> = {
    limassol: "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d205.07519633715472!2d33.047921464833614!3d34.67482215483466!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x14e733ac485738eb%3A0x2bf26bab8beb81a7!2sChess%20Grinder!5e0!3m2!1sen!2s!4v1777202322478!5m2!1sen!2s",
    berlin: "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2187.651783795628!2d13.408156497275556!3d52.527073439803885!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47a84fb3e5360885%3A0x77845dd97dd0242f!2sChess%20Grinder!5e0!3m2!1sen!2s!4v1777202818486!5m2!1sen!2s",
    tbilisi: "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d5958.753933972758!2d44.793516093579115!3d41.69079720000002!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x40440d4a13bc1a3f%3A0x36b77d13f1282a5a!2sSecret%20lounge!5e0!3m2!1sen!2s!4v1777202908448!5m2!1sen!2s",
};

export default function TournamentLocationMap({city}: {city?: string | null}) {
    if (!city) return null;
    const src = CITY_MAP_EMBED_URLS[city.trim().toLowerCase()];
    if (!src) return null;
    return (
        <div className={"w-full aspect-[4/3] sm:aspect-[16/9] max-h-[450px]"}>
            <iframe
                title={`${city} location`}
                src={src}
                className={"w-full h-full border-0"}
                allowFullScreen
                loading="lazy"
                referrerPolicy="no-referrer-when-downgrade"
            />
        </div>
    );
}
