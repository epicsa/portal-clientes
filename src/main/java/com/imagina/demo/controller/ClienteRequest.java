package com.imagina.demo.controller;

/**
 * Datos de entrada de un alta de cliente. Cualquier otro campo del JSON se ignora.
 */
public record ClienteRequest(String nombre, String email) {
}
