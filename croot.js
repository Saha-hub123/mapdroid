/**
 * croot.js - Dynamic GitHub Release Fetcher & Landing Page Controller
 * Built using the crootjs / jscroot modular ES6+ pattern.
 */

import { getJSON } from "https://cdn.jsdelivr.net/gh/jscroot/lib@0.2.8/api.js";
import { setInner, onClick } from "https://cdn.jsdelivr.net/gh/jscroot/lib@0.2.8/element.js";

// Repository configuration
const REPO_OWNER = "Saha-hub123";
const REPO_NAME = "mapdroid";
const GITHUB_API_LATEST_RELEASE = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/releases/latest`;
const DIRECT_DOWNLOAD_FALLBACK = `https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/latest/download/mapdroid.apk`;
const RELEASES_PAGE_URL = `https://github.com/${REPO_OWNER}/${REPO_NAME}/releases`;

/**
 * Format bytes to readable string (e.g. 15.4 MB)
 */
function formatBytes(bytes, decimals = 1) {
    if (!bytes || bytes === 0) return '0 B';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Format ISO date string into readable Indonesian format
 */
function formatDate(isoString) {
    if (!isoString) return "-";
    const date = new Date(isoString);
    return date.toLocaleDateString("id-ID", {
        day: "numeric",
        month: "long",
        year: "numeric"
    });
}

/**
 * Callback function to process GitHub Release JSON data
 */
function handleReleaseData(data) {
    if (!data || !data.tag_name) {
        console.warn("No release data received or rate limit hit. Using fallback link.");
        setupFallback();
        return;
    }

    const tagName = data.tag_name;
    const releaseName = data.name || tagName;
    const publishedAt = formatDate(data.published_at);
    
    // Find APK asset
    let apkAsset = null;
    if (data.assets && data.assets.length > 0) {
        // Look for mapdroid.apk or any .apk file
        apkAsset = data.assets.find(asset => asset.name === "mapdroid.apk") 
                || data.assets.find(asset => asset.name.endsWith(".apk"))
                || data.assets[0];
    }

    const downloadUrl = apkAsset ? apkAsset.browser_download_url : DIRECT_DOWNLOAD_FALLBACK;
    const fileSize = apkAsset ? formatBytes(apkAsset.size) : "~18 MB";
    const downloadCount = apkAsset ? (apkAsset.download_count || 0) : 0;

    // Update UI elements using croot's setInner
    setInner("release-tag", tagName);
    setInner("hero-version-badge", `Rilis Terbaru: ${tagName}`);
    setInner("release-date", publishedAt);
    setInner("apk-size", fileSize);
    setInner("download-count", `${downloadCount} unduhan`);
    setInner("direct-link-url", downloadUrl);

    // Update download buttons href
    const downloadBtn = document.getElementById("download-btn");
    if (downloadBtn) {
        downloadBtn.href = downloadUrl;
    }
    const navDownloadBtn = document.getElementById("nav-download-btn");
    if (navDownloadBtn) {
        navDownloadBtn.href = downloadUrl;
    }

    // Generate QR Code for direct APK download
    updateQrCode(downloadUrl);
}

/**
 * Fallback values in case the release is still building or API is offline
 */
function setupFallback() {
    setInner("release-tag", "v1.0.x (Latest)");
    setInner("hero-version-badge", "Rilis Terbaru Siap Unduh");
    setInner("release-date", "Rilis Otomatis GitHub");
    setInner("apk-size", "~18 MB");
    setInner("download-count", "Aktif");
    setInner("direct-link-url", DIRECT_DOWNLOAD_FALLBACK);

    const downloadBtn = document.getElementById("download-btn");
    if (downloadBtn) {
        downloadBtn.href = DIRECT_DOWNLOAD_FALLBACK;
    }
    const navDownloadBtn = document.getElementById("nav-download-btn");
    if (navDownloadBtn) {
        navDownloadBtn.href = DIRECT_DOWNLOAD_FALLBACK;
    }

    updateQrCode(DIRECT_DOWNLOAD_FALLBACK);
}

/**
 * Generate QR code image using quickchart qr API
 */
function updateQrCode(url) {
    const qrImg = document.getElementById("qr-code-img");
    if (qrImg) {
        qrImg.src = `https://quickchart.io/qr?text=${encodeURIComponent(url)}&size=160&margin=1`;
    }
}

/**
 * Initialize event handlers and listeners
 */
function initEvents() {
    // Copy download link button
    onClick("btn-copy-link", () => {
        const linkElem = document.getElementById("direct-link-url");
        const urlToCopy = linkElem ? linkElem.innerText : DIRECT_DOWNLOAD_FALLBACK;

        navigator.clipboard.writeText(urlToCopy).then(() => {
            const toast = document.getElementById("copy-toast");
            if (toast) {
                toast.classList.remove("opacity-0", "translate-y-2");
                toast.classList.add("opacity-100", "translate-y-0");
                setTimeout(() => {
                    toast.classList.remove("opacity-100", "translate-y-0");
                    toast.classList.add("opacity-0", "translate-y-2");
                }, 2500);
            }
        }).catch(err => {
            console.error("Gagal menyalin tautan: ", err);
        });
    });

    // Smooth scroll for anchors
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href');
            if (targetId.length > 1) {
                e.preventDefault();
                const targetElement = document.querySelector(targetId);
                if (targetElement) {
                    targetElement.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });
}

/**
 * Main application initialization
 */
function init() {
    initEvents();

    try {
        // Fetch release data from GitHub API using crootjs getJSON
        getJSON(GITHUB_API_LATEST_RELEASE, (data) => {
            if (data && !data.message) {
                handleReleaseData(data);
            } else {
                setupFallback();
            }
        });
    } catch (err) {
        console.warn("Error calling getJSON, using fallback:", err);
        setupFallback();
    }
}

// Run when DOM is ready
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
} else {
    init();
}
