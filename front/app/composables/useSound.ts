export function useSound() {
    const sounds = {
        click: new Audio('/sounds/click.mp3'),
        error: new Audio('/sounds/error.mp3'),
        win: new Audio('/sounds/win.mp3'),
        notification: new Audio('/sounds/notification.mp3')
    };

    sounds.error.volume = 0.4;
    sounds.click.volume = 0.1;
    sounds.win.volume = 0.1;
    sounds.notification.volume = 0.3;

    const play = (soundName: string | number) => {
        const audio = sounds[soundName];
        if (audio) {
            audio.currentTime = 0;
            audio.play().catch((e: any) => console.warn("Interação de áudio bloqueada", e));
        }
    };

    return { play };
}