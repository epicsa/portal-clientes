package com.imagina.demo.controller;

/**
 * Datos de un cliente que devuelve la API.
 */
public record ClienteResponse(Long id, String nombre, String email) {
}
